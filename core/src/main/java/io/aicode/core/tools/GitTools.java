package io.aicode.core.tools;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by liuyang on 2017/12/27.
 */
@Slf4j
public class GitTools {

    /**
     * 创建仓库
     * <p>
     * 如果仓库不存在就创建仓库
     * 仓库存在，返回存在的仓库
     *
     * @param repoDir 仓库文件夹
     * @return 仓库实例
     */
    public static Repository createGitRepository(File repoDir) {
        Repository ret = null;
        File repoGitDir = getGitAbsolutePath(repoDir);
        try {
            if (!repoGitDir.exists()) {
                //创建本地仓的库
                ret = FileRepositoryBuilder.create(repoGitDir);
                ret.create();
            } else {
                ret = openGitRepository(repoDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 创建仓库
     *
     * @param dirPath 仓库文件夹路径
     * @return 仓库实例
     */
    public static Repository createGitRepository(String dirPath) {
        return createGitRepository(new File(dirPath));
    }

    /**
     * 删除仓库
     *
     * @param repoDir 仓库文件夹
     * @return
     */
    public static boolean deleteGitRepository(File repoDir) {
        boolean ret = false;
        if (repoDir.exists()) {
            deleteFolder(repoDir);
            ret = repoDir.delete();
        }
        return ret;
    }

    /**
     * 删除仓库
     *
     * @param repoDir
     * @return
     */
    public static boolean deleteGitRepository(String repoDir) {
        File repoGitDir = getGitAbsolutePath(new File(repoDir));
        Repository repository = null;
        try {
            repository = new FileRepository(repoGitDir.getAbsoluteFile());
            try (Git git = new Git(repository)) {
                git.branchDelete().setBranchNames("master").setForce(true).call();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotDeleteCurrentBranchException e) {
            e.printStackTrace();
        } catch (NotMergedException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 打开仓库
     *
     * @param repoDir 仓库文件夹
     * @return
     */
    public static Repository openGitRepository(File repoDir) {
        try {
            File file = getGitAbsolutePath(repoDir);
            if (file.exists()) {
                return new FileRepository(file);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 打开仓库
     *
     * @param dirPath
     * @return
     */
    public static Repository openGitRepository(String dirPath) {
        return openGitRepository(new File(dirPath));
    }

    /**
     * 克隆远程仓库（私有，需要用户名、密码）
     * 本地不能有相同名的仓库
     *
     * @param httpsUrl https地址
     * @param repoDir  仓库文件夹
     * @param username git用户名
     * @param password git密码
     * @return
     */
    public static boolean cloneGit(String httpsUrl, File repoDir, String username, String password) {
        boolean ret = false;
        Git git = null;
        try {
            CloneCommand cloneCommand = Git.cloneRepository().setURI(httpsUrl);
            if (username != null && password != null) {
                cloneCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
            }
            if (repoDir != null) {
                //目录为null则克隆到当前项目下的目录，不建议这样做
                cloneCommand.setDirectory(repoDir);
            }
            git = cloneCommand.call();
            ret = true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (git != null) {
                git.close();
            }
        }
        return ret;
    }

    public static boolean cloneGit(String httpsUrl, String dirPath, String username, String password) {
        return cloneGit(httpsUrl, new File(dirPath), username, password);
    }

    /**
     * 克隆远程仓库（公共，不需要用户名、密码）
     * 本地不能有相同名的仓库
     *
     * @param httpsUrl https地址
     * @param repoDir  仓库文件夹
     * @return
     */
    public static boolean cloneGit(String httpsUrl, File repoDir) {
        return cloneGit(httpsUrl, repoDir, null, null);
    }

    public static boolean cloneGit(String httpsUrl, String dirPath) {
        return cloneGit(httpsUrl, new File(dirPath), null, null);
    }

    /**
     * 检出仓库
     * 本地必须有仓库
     *
     * @param repoDir    仓库文件夹
     * @param branchName 分支全名
     * @return
     */
    public static boolean checkoutGit(File repoDir, String branchName) {
        Repository repository = null;
        try {
            if (branchName == null) {
                branchName = "master";
            }
            File repoGitDir = getGitAbsolutePath(repoDir);
            if (!repoGitDir.exists()) {
                log.error("仓库不存在");
                return false;
            } else {
                repository = openGitRepository(repoDir);
                PullCommand pullCommand;
                try (Git git = new Git(repository)) {
                    CheckoutCommand checkoutCommand = git.checkout();
                    checkoutCommand.setName(branchName).call();
                    pullCommand = git.pull();
                }
                pullCommand.call();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (repository != null) {
                repository.close();
            }
        }
    }

    public static boolean checkoutGit(String repoDir, String branchName) {
        return checkoutGit(new File(repoDir), branchName);
    }

    public static boolean checkoutGit(File repoDir) {
        return checkoutGit(repoDir, null);
    }

    public static boolean checkoutGit(String repoDir) {
        return checkoutGit(repoDir, null);
    }


    /**
     * 获取仓库状态
     *
     * @param repoDir 仓库文件夹
     * @return
     */
    public static Status getGitStatus(File repoDir) {
        Status ret = null;
        Repository repository = null;
        try {
            File repoGitDir = getGitAbsolutePath(repoDir);
            if (!repoGitDir.exists()) {
                log.error("仓库不存在");
                return null;
            } else {
                repository = new FileRepository(repoGitDir.getAbsoluteFile());
                try (Git git = new Git(repository)) {
                    ret = git.status().call();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (repository != null) {
                repository.close();
            }
        }
        return ret;
    }

    public static Status getGitStatus(String dirPath) {
        return getGitStatus(new File(dirPath));
    }

    /**
     * pull
     *
     * @param repoDir
     * @return
     */
    public static boolean pullGit(File repoDir) {
        boolean ret = false;
        File repoGitDir = getGitAbsolutePath(repoDir);
        if (!repoGitDir.exists()) {
            log.error("仓库不存在");
            return false;
        } else {
            Repository repository = null;
            try {
                repository = openGitRepository(repoDir);
                PullCommand pullCommand;
                try (Git git = new Git(repository)) {
                    pullCommand = git.pull();
                }
                pullCommand.call();
                ret = true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (repository != null) {
                    repository.close();
                }
            }
        }
        return ret;
    }

    /**
     * 创建新分支
     *
     * @param branchName
     * @param repoDir
     * @return
     */
    public static boolean createBranch(String branchName, File repoDir) {
        boolean ret = false;
        String newBranchIndex = "refs/heads/" + branchName;
        String gitPathURI = "";
        File repoGitDir = getGitAbsolutePath(repoDir);
        if (!repoGitDir.exists()) {
            log.error("仓库不存在");
            return false;
        } else {
            Repository repository = null;
            Git git = null;
            try {
                repository = new FileRepository(repoGitDir.getAbsoluteFile());
                git = new Git(repository);
                //检查分支是否存在
                List<Ref> refList = git.branchList().call();
                for (Ref ref : refList) {
                    if (ref.getName().equals(newBranchIndex)) {
                        git.branchDelete().setBranchNames(branchName).setForce(true).call();
                        break;
                    }
                }
                //新建分支
                Ref ref = git.branchCreate().setName(branchName).call();
                //推送到远程
                git.push().add(ref).call();
                ret = true;
            } catch (Exception e) {
                log.error(e.getMessage());
                return false;
            } finally {
                if (repository != null) {
                    repository.close();
                }
                if (git != null) {
                    git.close();
                }
            }
        }
        return ret;
    }

    public static boolean createBranch(String branchName, String dirPath) {
        return createBranch(branchName, new File(dirPath));
    }

    /**
     * 提交并推送远程仓库
     *
     * @param repoDir  本地仓库文件夹
     * @param message  提交信息
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static boolean commitAndPush(File repoDir, String username, String password, String message) {
        boolean ret = false;
        File repoGitDir = getGitAbsolutePath(repoDir);
        if (!repoGitDir.exists()) {
            log.info("仓库不存在");
            return false;
        } else {
            Repository repository = null;
            Git git = null;
            try {
                repository = new FileRepository(repoGitDir.getAbsoluteFile());
                git = new Git(repository);
                Status repoStatus = getGitStatus(repoDir);
                Set<String> untractedFiles = repoStatus.getUntracked();
                for (String fileName : untractedFiles) {
                    git.add().addFilepattern(fileName).call();
                }
                git.commit().setMessage(message).call();
                PushCommand pushCommand = git.push();
                pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
                pushCommand.call();
                ret = true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (repository != null) {
                    repository.close();
                }
                if (git != null) {
                    git.close();
                }
            }
        }
        return ret;
    }

    public static boolean commitAndPush(String repoDir, String username, String password, String message) {
        return commitAndPush(new File(repoDir), username, password, message);
    }

    /**
     * 获取git绝对路径
     *
     * @param repoDir
     * @return
     */
    private static File getGitAbsolutePath(File repoDir) {
        return new File(repoDir.getAbsoluteFile() + "/.git");
    }

    /**
     * 删除文件夹
     *
     * @param file
     */
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

    public static void main(String[] args) throws IOException {
        String username = "vevoly";
        String password = "liuyang3311680";
        String authorName = "vevoly";
        String authorEmail = "jevoly@163.com";
        String dirPath = "c:/repoTest/";
        String clonePath = "c:/JGitTest0";
        String httpsUrl = "https://github.com/vevoly/allinpayView.git";
        httpsUrl = "https://gitee.com/vevoly/git-workflow-test.git";
        String sshUrl = "git@github.com:vevoly/allinpayView.git";
        //新建仓库
        /*Repository newRepo = createGitRepository(dirPath);
        log.error("新建或打开已有的仓库，分支名为：" + newRepo.getBranch());*/
        //删除仓库
        /*deleteGitRepository(dirPath);*/
        //仓库状态
        /*Status repoStatus = getGitStatus(dirPath);
        log.error("添加的文件：" + repoStatus.getAdded());
        log.error("修改的文件：" + repoStatus.getModified());
        log.error("改变的文件：" + repoStatus.getChanged());
        log.error("忽略的文件：" + repoStatus.getIgnoredNotInIndex());
        log.error("删除的文件：" + repoStatus.getRemoved());
        log.error("未提交文件：" + repoStatus.getUncommittedChanges());*/
        //克隆
        /*if (cloneGit(httpsUrl, clonePath, username, password)) {
            log.error("clone完成\n目录：" + clonePath);
        } else {
            log.error("clone失败");
        }*/
        //检出
        /*if (checkoutGit(clonePath)) {
            log.error("检出成功\n目录：" + clonePath);
        } else {
            log.error("检出失败");
        }*/
        //创建分支
        if (createBranch("dev", clonePath)) {
            log.error("创建新分支成功\n分支名：dev");
        } else {
            log.error("创建新分支失败");
        }
        //打开仓库
        Repository repo = openGitRepository(dirPath);
        log.error("新打开仓库分支：" + repo.getBranch());

        //提交
        /*if (commitAndPush(new File(clonePath), username, password, "jgit提交测试")) {
            log.error("提交成功");
        } else {
            log.error("提交失败");
        }*/

        String gitPath = "https://gitee.com/helixin/aicode_template.git";
        System.out.println(gitPath.substring(gitPath.lastIndexOf("/") + 1).replace(".git", ""));
    }


}
