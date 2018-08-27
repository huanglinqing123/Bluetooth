package hlq.base.bean;

/**
 * Created by Huanglinqing on 2018/8/25/025 20:58
 * E-Mail Address：1306214077@qq.com
 * 发送消息 Eventbus对应的实体类
 */
public class MessageBean {

    private int id;
    private String content;

    public MessageBean(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageBean(int id, String content) {
        this.id = id;
        this.content = content;
    }
}
