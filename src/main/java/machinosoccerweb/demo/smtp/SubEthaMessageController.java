package machinosoccerweb.demo.smtp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class SubEthaMessageController {
  private final SubEthaMessageCollection subEthaMessageCollection;

  @Autowired
  public SubEthaMessageController(SubEthaMessageCollection subEthaMessageCollection) {
    this.subEthaMessageCollection = subEthaMessageCollection;
  }

  @RequestMapping("/messages")
  public String messages(Model model) {
    model.addAttribute("messages", subEthaMessageCollection.getMessages());

    return "demo/messages";
  }

  @RequestMapping("messages/{index}/download")
  public void download(@PathVariable("index") int index, HttpServletResponse response)
      throws IOException {
    response.setContentType("message/rfc822;charset=utf-8");
    response.setHeader("Content-Disposition", "attachment; filename=email"+index+".eml");
    try (Writer writer = response.getWriter()) {
      writer.write(subEthaMessageCollection.getMessage(index).getContent());
    }
  }

  @RequestMapping("/messages/{index}")
  public String messageContent(@PathVariable("index") int index, Model model) {
    model.addAttribute("message", subEthaMessageCollection.getMessage(index));

    return "demo/messageContent";
  }
}
