package io.aicode.core.tools;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by liuyang on 2017/12/26.
 */
public class JGitTools {

    public static String localRepoPath = "C:/repo";
    public static String localRepoGitConfig = "C:/repo/.git";
    public static String remoteRepoURI = "git@github.com:vevoly/test.git";
    public static String localCodeDir = "C:/platplat";

    /**
     * 建立与远程仓库的联系，仅需执行一次
     * @throws GitAPIException
     */
    public static void setupRepo() throws GitAPIException {
        Git git = Git.cloneRepository().setURI(remoteRepoURI).setDirectory(new File(localRepoPath)).call();
    }

    /**
     * 新建分支并同步到远程仓库
     * @param branchName    分支名称
     * @return
     */
    public static String newBranch(String branchName) {
        String newBranchIndex = "refs/heads/" + branchName;
        String gitPathURI = "";
        Git git = null;
        try {
            //检查新建的分支是否存在
            List<Ref> refList = git.branchList().call();
            for (Ref ref : refList) {
                if (ref.getName().equals(newBranchIndex)) {
                    System.out.println("Removing branch before");
                    git.branchDelete().setBranchNames(branchName).setForce(true).call();
                    break;
                }
            }
            //新建分支
            Ref ref = git.branchCreate().setName(branchName).call();
            //推送到远程
            git.push().add(ref).call();
            gitPathURI = remoteRepoURI + " " + "feature/" + branchName;

        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return gitPathURI;
    }

    /**
     * 提交文件并同步到远程仓库
     * @throws IOException
     * @throws GitAPIException
     */
    public static void commitFiles() throws IOException, GitAPIException {
        String filePath = null;
        //打开本地git
        Git git = Git.open(new File(localRepoGitConfig));
        //创建用户文件
        File file = new File(filePath);
        file.createNewFile();
        git.add().addFilepattern("pets").call();
        //提交
        git.commit().setMessage("added pets").call();
        //推送远程
        git.push().call();
    }

    /**
     * 根据地址从远程仓库拉取代码到本地
     * @param cloneURL
     * @return  boolean
     */
    public static boolean pullBranchToLocal(String cloneURL) {
        boolean resultFlat = false;
        Git git = null;
        String[] splitURL = cloneURL.split(" ");
        String branchName = splitURL[1];
        String fileDir = localCodeDir + "/" + branchName;
        //检查目标文件夹是否存在
        File file = new File(fileDir);
        if (file.exists()) {
            deleteFolder(file);
        }
        try {
            git = Git.open(new File(localRepoGitConfig));
            git.cloneRepository().setURI(cloneURL).setDirectory(file).call();
            resultFlat = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return resultFlat;
    }

    private static void deleteFolder(File file) {
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFolder(files[i]);
                files[i].delete();
            }
        }
    }

    public static void main(String[] args) throws GitAPIException {
        //JGitTools.setupRepo();
        JGitTools.newBranch("hello");
    }
}
