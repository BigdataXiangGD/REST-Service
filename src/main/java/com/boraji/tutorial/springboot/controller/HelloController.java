package com.boraji.tutorial.springboot.controller;

import com.boraji.tutorial.springboot.CyclometicCalculation;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class HelloController {

    private static final String LOCAL_CLONE_REPO = "/Users/weidian13/Desktop/Java/Restful/src/repo/";

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/hello")
    public String sayHello(@RequestParam("name") String name, Model model) throws IOException, NoHeadException {
        Integer sum = 0;
        Git.cloneRepository().setURI(name)
                .setDirectory(new File(LOCAL_CLONE_REPO)).setCloneAllBranches(true)
                .call();
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(LOCAL_CLONE_REPO + ".git"))
                .readEnvironment().findGitDir().build();
        Git git = new Git(repository);

        RevWalk walk = new RevWalk(repository);

        List<Ref> branches = git.branchList().call();
        for (Ref branch : branches) {
            String branchName = branch.getName();

            System.out.println("Commits of branch: " + branch.getName());
            System.out.println("-------------------------------------");

            Iterable<RevCommit> commits = git.log().call();

            for (RevCommit commit : commits) {
                RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
                for (Map.Entry<String, Ref> e : repository.getAllRefs().entrySet()) {
                    if (e.getKey().startsWith(Constants.R_HEADS) && walk.isMergedInto(targetCommit, walk.parseCommit(e.getValue().getObjectId()))) {
                        String foundInBranch = e.getValue().getName();
                        if (branchName.equals(foundInBranch)) {
                            RevTree tree = targetCommit.getTree();
                            TreeWalk treeWalk = new TreeWalk(repository);
                            treeWalk.addTree(tree);
                            treeWalk.setRecursive(false);
                            System.out.println("Commit found: " + commit.getName());
                            while (treeWalk.next()) {
                                ObjectId objectId = treeWalk.getObjectId(0);
                                ObjectLoader loader = repository.open(objectId);
                                File newFile = new File("tempReadFile");
                                loader.copyTo(new FileOutputStream(newFile));
                                CyclometicCalculation cal = new CyclometicCalculation();
                                sum += cal.calculateCC(newFile);
                            }
                            break;
                        }
                    }
                }
            }
        }
        model.addAttribute("name", sum);
        return "hello";
    }
}
