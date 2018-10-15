package io.aicode.project.ctrl;

import com.jcraft.jsch.*;
import io.aicode.core.tools.SSH2;
import io.aicode.core.tools.SSHResInfo;

import java.io.*;
import java.util.Scanner;

/**
 * Created by lixin on 2018/9/30.
 */
public class Test {


    public static void test() {

        String host = "192.168.1.220";
        int port = 22;
        String user = "pitop";
        String password = "0";

        String command1 = "ls -ltr";
        try {

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();

            System.out.println("Connected");
            ChannelShell channel = (ChannelShell) session.openChannel("shell");
            InputStream in = channel.getInputStream();
            channel.setPty(true);

            channel.connect();

            OutputStream os = channel.getOutputStream();
            os.write((command1 + "\r\n").getBytes());
            os.flush();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    String s = new String(tmp, 0, i);
                    if (s.indexOf("--More--") >= 0) {
                        os.write((" ").getBytes());
                        os.flush();
                    }
                    System.out.print(s);
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            os.close();
            in.close();
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void test2() {
        SSH2 ssh = null;
        while (true) {
            Scanner scan = new Scanner(System.in);
            String read = scan.nextLine();
            if (read != null) {
                try {

                    if (ssh == null) {
                        //使用目标服务器机上的用户名和密码登陆
                        ssh = new SSH2("192.168.1.220", 22, "pitop", "0");
                    }
                    try {
                        SSHResInfo resInfo = ssh.shell(read, 200);
                        System.out.println(resInfo.toString());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        ssh.close();
                    }
                } catch (JSchException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    public static void test3() throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession("pitop", "192.168.1.220", 22);
        session.setPassword("0");
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(50000);

        Channel channel = session.openChannel("shell");
//        InputStream inputStream = new ByteArrayInputStream(new String("ps -ef").getBytes());
//        channel.setInputStream(inputStream);
        channel.setInputStream(System.in);
//        FileOutputStream fileOutputStream=new FileOutputStream(File.createTempFile("aaa","txt",new File("C:\\")));
//        channel.setOutputStream(fileOutputStream);
        InputStream inputStream1 = channel.getInputStream();
        byte[] tmp1 = new byte[1024];

//        OutputStream outputStream = new ByteArrayOutputStream();
//        channel.setOutputStream(outputStream);
//        byte[] tmp = new byte[1024];
//        outputStream.write(tmp);
//        outputStream.flush();

//        InputStream inputStream = new ByteArrayInputStream(tmp);
        channel.connect(3 * 1000);


        new Thread() {
            public void run() {
                try {
                    while (true) {
                        while (inputStream1.available() > 0) {
                            int i = inputStream1.read(tmp1, 0, 1024);
                            System.out.println("111111111111");
                            System.out.println(new String(tmp1, 0, i));
                        }
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


    public static void test4() throws JSchException, IOException {
        JSch jsch = new JSch();
        Session session = jsch.getSession("pitop", "192.168.1.220", 22);
        session.setPassword("0");
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(50000);

        Channel channel = session.openChannel("shell");
//        InputStream inputStream = new ByteArrayInputStream(new String("ps -ef").getBytes());
//        channel.setInputStream(inputStream);
        channel.setInputStream(System.in);
//        FileOutputStream fileOutputStream=new FileOutputStream(File.createTempFile("aaa","txt",new File("C:\\")));
//        channel.setOutputStream(fileOutputStream);
        InputStream inputStream1 = channel.getInputStream();
        byte[] tmp1 = new byte[1024];

//        OutputStream outputStream = new ByteArrayOutputStream();
//        channel.setOutputStream(outputStream);
//        byte[] tmp = new byte[1024];
//        outputStream.write(tmp);
//        outputStream.flush();

//        InputStream inputStream = new ByteArrayInputStream(tmp);
        channel.connect(3 * 1000);


        Scanner scanner = new Scanner(inputStream1);
        while (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }


    }


    static Session session = null;
    static Channel channel = null;
    static PrintWriter sshout;  // SSH 輸出端
    static Scanner scan = null;

    public static String test5(String cmd) throws JSchException, IOException {
        if (session == null) {
            JSch jsch = new JSch();
            session = jsch.getSession("pitop", "192.168.1.220", 22);
            session.setPassword("0");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(50000);

            channel = session.openChannel("shell");
            PipedInputStream pipedInputStream;
            PipedOutputStream pipedOutputStream;
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream.connect(pipedOutputStream);
            channel.setInputStream(pipedInputStream);
            sshout = new PrintWriter(pipedOutputStream, true);

            // 创建输出通道
            pipedInputStream = new PipedInputStream();
            pipedOutputStream = new PipedOutputStream();
            pipedInputStream.connect(pipedOutputStream);
            channel.setOutputStream(pipedOutputStream);
            scan = new Scanner(pipedInputStream, "UTF-8");
            channel.connect(3 * 1000);
            sshout.print("\n\n");
            sshout.flush();
        }

        sshout.println(cmd);
        sshout.flush();
        StringBuffer stringBuffer = new StringBuffer();
        boolean flag = true;
        while (flag) {
            if (scan.hasNextLine()) {
                String r = scan.nextLine();
//                if (r.contains("~")) {
//                    break;
//                }
                stringBuffer.append(r);
                stringBuffer.append("\n");
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String result = stringBuffer.toString();
        System.out.println(result);

        String line = null;
//        do {
//            line = scan.nextLine();
//        } while (!line.matches("^[0-9]+$"));

        return result;
    }

    public static void main(String[] args) throws Exception {


//        test();
//        test2();
//        test3();
//        test4();

        while (true) {
            Scanner scan = new Scanner(System.in);
            String read = scan.nextLine();
            if (read != null) {
                String result = test5(read);
                System.out.println(result);
            }
        }

//        SSHClient sshClient = null;
//        while (true) {
//            Scanner scan = new Scanner(System.in);
//            String read = scan.nextLine();
//            if (read != null) {
//                if (sshClient == null) {
//                    sshClient = new SSHClient("192.168.1.220", "pitop", "0");
//                }
//                String result = sshClient.execute(read);
//                System.out.println(result);
//            }
//        }


    }
}
