package io.github.psgs.issuesdownload;

import io.github.psgs.issuesdownload.gui.GUI;
import org.kohsuke.github.*;

import java.io.FileWriter;
import java.io.IOException;

public class IssuesDownload {

    public static void main(String[] args) {
        try {
            Config.loadConfiguration();
        } catch (IOException ex) {
            System.out.println("An IOException occurred while loading the configuration!");
            ex.printStackTrace();
        }
        GUI.main(args);
    }

    public static String saveIssues(String repoDetails, GHIssueState issueState) {

        String[] repoInfo = repoDetails.split("/");

        try {
            GitHub github = GitHub.connectUsingOAuth(Config.githubtoken);
            GHRepository repository = github.getUser(repoInfo[0]).getRepository(repoInfo[1]);

            FileWriter writer = new FileWriter("issues.csv");
            writer.append("Id, URL, Title, Creator, Assignee, Milestone, State, Labels");
            writer.append("\n");

            for (GHIssue issue : repository.getIssues(issueState)) {
                writer.append(String.valueOf(issue.getNumber()) + ",");
                writer.append("http://github.com/" + repoInfo[0] + "/" + repoInfo[1] + "/issues/" + String.valueOf(issue.getNumber()) + ",");
                writer.append(issue.getTitle() + ",");
                writer.append(issue.getUser().getLogin() + ",");
                if (issue.getAssignee() != null) {
                    writer.append(issue.getAssignee().getLogin() + ",");
                } else {
                    writer.append(" ,");
                }
                if (issue.getMilestone() != null) {
                    writer.append(issue.getMilestone().getTitle() + ",");
                } else {
                    writer.append(" ,");
                }
                writer.append(issue.getState() + ",");

                for (org.kohsuke.github.GHLabel label : issue.getLabels()) {
                    writer.append(label.getName() + " ");
                }

                //writer.append(issue.getBody() + ",");
                writer.append("\n");
            }
            writer.flush();
            writer.close();
            return "Download Complete!";
        } catch (IOException ex) {
            System.out.println("An IOException has occurred!");
            ex.printStackTrace();
            if (ex.getMessage().equalsIgnoreCase("api.github.com")) {
                return "An error has occurred reaching " + ex.getMessage() + "! Please check your network connection.";
            }
        }
        return "An error has occurred!";
    }
}
