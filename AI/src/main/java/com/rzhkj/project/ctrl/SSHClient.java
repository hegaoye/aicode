package com.rzhkj.project.ctrl;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * 可切換 root 的 SSH 客戶端
 * <p>
 * 已測試作業系統:
 * 1. Ubuntu 8.04
 * 2. RedHat EL4
 *
 * @author Raymond Wu (小璋丸)
 */
public class SSHClient {

    // 預設的提示字串
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String DEFAULT_PASSPS = "(Password|密碼)[:：] ";
    private static final String ANSI_CONTROL = "\\[[0-9]{0,2};?[0-9]{0,2}m";
    private static final String REGEX_KEYCHARS = "[]{}()^$?+*.&|";

    private boolean isroot;     // 檢查目前是否為 root
    private boolean connected;  // 檢查目前是否連線中
    private String userps;       // user 的 shell 提示字串
    private String rootps;       // root 的 shell 提示字串
    private Session session;     // SSH 連線
    private Scanner sshin;       // SSH 輸入端
    private PrintStream sshout;  // SSH 輸出端
    private StringBuffer conbuf; // 最後一個指令的執行結果

    public static void main(String[] args) {
        SSHClient sshClient = null;
        while (true) {
            Scanner scan = new Scanner(System.in);
            String read = scan.nextLine();
            if (read != null) {
                if (sshClient == null) {
                    //使用目标服务器机上的用户名和密码登陆
                    sshClient = new SSHClient("192.168.1.220", "pitop", "0");
                }
                try {
                    String result = sshClient.execute(read);
                    System.out.println(result);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    sshClient.close();
                }

            }
        }
    }

    /**
     * 建立 SSH 連線
     *
     * @param host     主機
     * @param user     帳號
     * @param password 密碼
     */
    public SSHClient(String host, String user, String password) {
        this(host, user, password, DEFAULT_CHARSET);
    }

    /**
     * 建立 SSH 連線, 採用指定的編碼方式輸出
     *
     * @param host     主機
     * @param user     帳號
     * @param password 密碼
     * @param charset  字串編碼
     */
    public SSHClient(String host, String user, String password, String charset) {
        PipedInputStream pipedInputStream;
        PipedOutputStream pipedOutputStream;

        try {
            // 設定連線方式
            JSch jsch = new JSch();
            session = jsch.getSession(user, host);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            ChannelShell channelShell = (ChannelShell) session.openChannel("shell");

            // 建立輸入端
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream.connect(pipedOutputStream);
            channelShell.setInputStream(pipedInputStream);
            sshout = new PrintStream(pipedOutputStream);

            // 建立輸出端
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream.connect(pipedOutputStream);
            channelShell.setOutputStream(pipedOutputStream);
            sshin = new Scanner(pipedInputStream, charset);

            // 連線到主機 (會 block)
            channelShell.connect();
            while (!channelShell.isConnected()) {
                if (channelShell.isClosed()) break;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }

            // 尋找相同的兩列視為 user 提示字串
            conbuf = new StringBuffer();
            sshout.print("\n\n");
            sshout.flush();
            String prev = "";
            String line = sshin.nextLine();
            while (line.indexOf(user) == -1 || !line.equals(prev)) {
                conbuf.append(prev);
                conbuf.append('\n');
                prev = line;
                line = sshin.nextLine();
            }
            conbuf.delete(0, 1);

            // 拿掉多出來的兩個空白行, 因 print("\n\n") 造成, 純粹美觀
            if (conbuf.substring(conbuf.length() - 2).equals("\n\n")) {
                conbuf.delete(conbuf.length() - 2, conbuf.length());
            }

            // 紀錄 user 提示字串
            int home = line.indexOf('~');
            userps = escapeRegex(line.substring(0, home));
            connected = true;
        } catch (JSchException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 切換到 root 並且移動到 root 家目錄
     *
     * @param password root 密碼
     */
    public boolean switchRoot(String password) {
        return switchRoot(password, DEFAULT_PASSPS);
    }

    /**
     * 切換到 root 並且移動到 root 家目錄, 偵測自訂的提示字串
     *
     * @param password root 密碼
     * @param passps   密碼輸入提示字串
     */
    public boolean switchRoot(String password, String passps) {
        String line;

        // 搜尋登入成功的提示字串
        sshout.print("su -\n");
        sshout.flush();

        // 搜尋密碼輸入的提示字串
        sshin.findWithinHorizon(passps, 0);
        sshout.print(password);
        sshout.print('\n');
        sshout.flush();

        // 檢查是否登入成功
        sshout.print("echo $?\n");
        sshout.flush();
        do {
            line = sshin.nextLine();
        } while (!line.matches("^[0-9]+$"));
        isroot = (Integer.parseInt(line) == 0);

        // 紀錄 root 提示字串
        if (isroot) {
            sshout.print("\n");
            line = sshin.nextLine();
            int home = line.indexOf('~');
            rootps = escapeRegex(line.substring(0, home));
        }

        return isroot;
    }

    /**
     * 執行一個指令, 傳回結束代碼 (只能執行不需要輸入的指令)
     *
     * @param command 指令字串
     * @return 結束代碼
     */
    public String execute(String command) {
        String currps = isroot ? rootps : userps;

        // 發送指令
        sshout.print(command);
        sshout.print("\n\n");
        sshout.flush();

        // 跳到指令之後
        sshin.findWithinHorizon(currps, 0);
        sshin.nextLine();

        // 接收輸出, 注意 currps 因為有設計 Regex 逸脫不可以用 indexOf 判斷
        currps = "^" + currps + ".+";
        conbuf.delete(0, conbuf.length());
        String line = sshin.nextLine();
        line = line.replaceAll(ANSI_CONTROL, "");
        while (!line.matches(currps)) {
            conbuf.append(line);
            conbuf.append('\n');
            line = sshin.nextLine();
            line = line.replaceAll(ANSI_CONTROL, "");
        }
        System.out.println(conbuf);

        // 送出取得回傳值的指令
        sshout.print("echo $?\n");
        sshout.flush();

        // 跳到指令之後
        do {
            line = sshin.nextLine();
        } while (!line.matches("^[0-9]+$"));

        return conbuf.toString();
//        return Integer.parseInt(line);
    }

    /**
     * 傳回最後一個指令的輸出畫面
     *
     * @return 輸出畫面
     */
    public String getLastOutput() {
        return conbuf.toString();
    }

    /**
     * 檢查連線狀態
     *
     * @return 連線成功或失敗
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * 結束 ssh 連線
     */
    public void close() {
        if (isroot) sshout.print("exit");
        session.disconnect();
        connected = false;
    }

    // 逸脫 Regex 的特殊字元
    private String escapeRegex(String s) {
        char ch;
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            ch = s.charAt(i);
            if (REGEX_KEYCHARS.indexOf(ch) > -1) result += "\\";
            result += ch;
        }

        return result;
    }


}