import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiscussionThread implements Serializable {

    private static final long serialVersionUID = 1L;

    public String threadId;
    public String title;
    public String content;
    public String authorUsername;
    public String authorRole;
    public boolean isPinned;
    public boolean isLocked;
    public List<Reply> replies;

    public DiscussionThread(String threadId, String title, String content,
                            String authorUsername, String authorRole) {
        this.threadId = threadId;
        this.title = title;
        this.content = content;
        this.authorUsername = authorUsername;
        this.authorRole = authorRole;
        this.isPinned = false;
        this.isLocked = false;
        this.replies = new ArrayList<>();
    }
}

class Reply implements Serializable{

    private static final long serialVersionUID = 1L;
    
    public String replyId;
    public String content;
    public String authorUsername;
    public String authorRole;

    public Reply(String replyId, String content, String authorUsername, String authorRole) {
        this.replyId = replyId;
        this.content = content;
        this.authorUsername = authorUsername;
        this.authorRole = authorRole;
    }

}
