package xor.bcmcgroup.service;

import xor.bcmcgroup.domain.Server;
import xor.bcmcgroup.repository.ServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Server.
 */
@Service
public class ServerService {

    private final Logger log = LoggerFactory.getLogger(ServerService.class);
    
    @Inject
    private ServerRepository serverRepository;

    /**
     * Save a server.
     *
     * @param server the entity to save
     * @return the persisted entity
     */
    public Server save(Server server) {
        log.debug("Request to save Server : {}", server);
        Server result = serverRepository.save(server);
        return result;
    }

    /**
     *  Get all the servers.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    public Page<Server> findAll(Pageable pageable) {
        log.debug("Request to get all Servers");
        Page<Server> result = serverRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one server by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    public Server findOne(String id) {
        log.debug("Request to get Server : {}", id);
        Server server = serverRepository.findOne(id);
        return server;
    }

    /**
     *  Delete the  server by id.
     *
     *  @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Server : {}", id);
        serverRepository.delete(id);
    }
}
