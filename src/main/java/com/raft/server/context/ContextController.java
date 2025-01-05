package com.raft.server.context;

import com.network.http.Http;
import com.raft.server.database.database.new_db.utils.RequestActionDTO;
import com.raft.server.node.peers.Peer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Console;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/context",produces = {MediaType.APPLICATION_JSON_VALUE})
@Api(tags="Context")
@RequiredArgsConstructor
class ContextController {

    private final Context context;
    private final Http http;
    @GetMapping
    @ApiOperation(value = "Get current node meta information")
    public Context getCurrentPeerState()  {
      return context;
    }

    @PostMapping("/stop")
    @ApiOperation(value = "Stop")
    public void stop()  {
        context.setActive(false);
    }

    @PostMapping("/start")
    @ApiOperation(value = "Start")
    public void start()  {
        context.setActive(true);
        Long leader = Long.valueOf(getLeader());
        http.callPost(context.getId().toString(), Object.class, String.class,"database", "delete_all");
        http.callGet(leader.toString(), Object.class,"database", "send_data", context.getId().toString());
    }

    @GetMapping("/is_leader")
    @ApiOperation(value = "Start")
    public Boolean isLeader()  {
        return context.isLeader();
    }

    @GetMapping("/leader")
    @ApiOperation(value = "Get isLeader ID")
    public Integer getLeader() {
        List<Integer> peersIds = context.getPeers().stream().map(Peer::getId).collect(Collectors.toList());
        Integer leader = context.getId();
        leader = peersIds.stream()
                .filter(i -> {
                    ResponseEntity<Boolean> response = http.callGet(i.toString(), Boolean.class, "context", "is_leader");
                    return response.getBody() != null && response.getBody();
                })
                .findFirst()
                .orElse(leader);
        return leader;
    }
}
