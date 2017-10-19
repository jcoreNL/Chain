package nl.johannisk.input.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import nl.johannisk.input.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
public class WebController {

    private final EurekaClient eurekaClient;
    private final TaskExecutor taskExecutor;

    @Autowired
    public WebController(final EurekaClient eurekaClient, final TaskExecutor taskExecutor) {
        this.eurekaClient = eurekaClient;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping(path = "/")
    public String index(final Model model) {
        model.addAttribute("message", new Message(0));
        addNodesToModel(model);
        return "index";
    }

    @GetMapping(path = "/increase")
    public String increase(final Model model) {
        model.addAttribute("message", new Message(0));
        addNodesToModel(model);
        Application application = eurekaClient.getApplication("jchain-node");
        List<InstanceInfo> instanceInfo = application.getInstances();
        for(InstanceInfo info : instanceInfo) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject("http://localhost:" + info.getPort() + "/api/difficulty/4", Object.class);
        }
        return "index";
    }

    @GetMapping(path = "/change")
    public String change(final Model model) {
        model.addAttribute("message", new Message(0));
        addNodesToModel(model);
        Application application = eurekaClient.getApplication("jchain-node");
        List<InstanceInfo> instanceInfo = application.getInstances();
        for(InstanceInfo info : instanceInfo) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject("http://localhost:" + info.getPort() + "/api/change", Object.class);
            break;
        }
        return "index";
    }

    @PostMapping(path = "/")
    public String message(@ModelAttribute final Message message, final Model model) {
        Application application = eurekaClient.getApplication("jchain-node");
        List<InstanceInfo> instanceInfo = application.getInstances();
        for(InstanceInfo info : instanceInfo) {
            taskExecutor.execute(new NodeInformerTask(Integer.toString(info.getPort()), message));
        }
        model.addAttribute("message", new Message(message.getIndex() + 1));
        addNodesToModel(model);
        return "index";
    }

    private void addNodesToModel(final Model model) {
        Application application = eurekaClient.getApplication("jchain-node");
        List<InstanceInfo> instanceInfo = application.getInstances();
        List<String> hosts = instanceInfo.stream()
                .filter(i -> i.getStatus().equals(InstanceInfo.InstanceStatus.UP))
                .sorted(Comparator.comparingInt(InstanceInfo::getPort))
                .map(m -> "localhost:" + m.getPort())
                .collect(Collectors.toList());
        model.addAttribute("hosts", hosts);
    }

    private class NodeInformerTask implements Runnable {

        private final String host;
        private final Message message;
        private final int delay;

        public NodeInformerTask(final String host, final Message message) {
            Random random = new Random();
            this.host = host;
            this.message = message;
            this.delay = random.nextInt(10000) + 3000;
        }

        public void run() {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject("http://localhost:" + host + "/node/message", message, Message.class);
        }
    }
}