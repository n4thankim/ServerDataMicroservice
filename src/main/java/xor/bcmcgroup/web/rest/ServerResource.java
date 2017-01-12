package xor.bcmcgroup.web.rest;

import com.codahale.metrics.annotation.Timed;
import xor.bcmcgroup.domain.Server;
import xor.bcmcgroup.service.ServerService;
import xor.bcmcgroup.web.rest.util.HeaderUtil;
import xor.bcmcgroup.web.rest.util.PaginationUtil;

import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Server.
 */
@RestController
@RequestMapping("/api")
public class ServerResource {

    private final Logger log = LoggerFactory.getLogger(ServerResource.class);
        
    @Inject
    private ServerService serverService;

    /**
     * POST  /servers : Create a new server.
     *
     * @param server the server to create
     * @return the ResponseEntity with status 201 (Created) and with body the new server, or with status 400 (Bad Request) if the server has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/servers")
    @Timed
    public ResponseEntity<Server> createServer(@Valid @RequestBody Server server) throws URISyntaxException {
        log.debug("REST request to save Server : {}", server);
        if (server.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("server", "idexists", "A new server cannot already have an ID")).body(null);
        }
        Server result = serverService.save(server);
        return ResponseEntity.created(new URI("/api/servers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("server", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /servers : Updates an existing server.
     *
     * @param server the server to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated server,
     * or with status 400 (Bad Request) if the server is not valid,
     * or with status 500 (Internal Server Error) if the server couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/servers")
    @Timed
    public ResponseEntity<Server> updateServer(@Valid @RequestBody Server server) throws URISyntaxException {
        log.debug("REST request to update Server : {}", server);
        if (server.getId() == null) {
            return createServer(server);
        }
        Server result = serverService.save(server);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("server", server.getId().toString()))
            .body(result);
    }

    /**
     * GET  /servers : get all the servers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of servers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/servers")
    @Timed
    public ResponseEntity<List<Server>> getAllServers(@ApiParam Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Servers");
        Page<Server> page = serverService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/servers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /servers/:id : get the "id" server.
     *
     * @param id the id of the server to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the server, or with status 404 (Not Found)
     */
    @GetMapping("/servers/{id}")
    @Timed
    public ResponseEntity<Server> getServer(@PathVariable String id) {
        log.debug("REST request to get Server : {}", id);
        Server server = serverService.findOne(id);
        return Optional.ofNullable(server)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /servers/:id : delete the "id" server.
     *
     * @param id the id of the server to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/servers/{id}")
    @Timed
    public ResponseEntity<Void> deleteServer(@PathVariable String id) {
        log.debug("REST request to delete Server : {}", id);
        serverService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("server", id.toString())).build();
    }

}
