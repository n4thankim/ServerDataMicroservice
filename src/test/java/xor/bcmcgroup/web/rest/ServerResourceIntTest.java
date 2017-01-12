package xor.bcmcgroup.web.rest;

import xor.bcmcgroup.ServerDataMicroserviceApp;

import xor.bcmcgroup.domain.Server;
import xor.bcmcgroup.repository.ServerRepository;
import xor.bcmcgroup.service.ServerService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ServerResource REST controller.
 *
 * @see ServerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerDataMicroserviceApp.class)
public class ServerResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URI = "AAAAAAAAAA";
    private static final String UPDATED_URI = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE = "BBBBBBBBBB";

    private static final String DEFAULT_RESPONSE_BLOB = "AAAAAAAAAA";
    private static final String UPDATED_RESPONSE_BLOB = "BBBBBBBBBB";

    @Inject
    private ServerRepository serverRepository;

    @Inject
    private ServerService serverService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restServerMockMvc;

    private Server server;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ServerResource serverResource = new ServerResource();
        ReflectionTestUtils.setField(serverResource, "serverService", serverService);
        this.restServerMockMvc = MockMvcBuilders.standaloneSetup(serverResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Server createEntity() {
        Server server = new Server()
                .name(DEFAULT_NAME)
                .uri(DEFAULT_URI)
                .response(DEFAULT_RESPONSE)
                .responseBlob(DEFAULT_RESPONSE_BLOB);
        return server;
    }

    @Before
    public void initTest() {
        serverRepository.deleteAll();
        server = createEntity();
    }

    @Test
    public void createServer() throws Exception {
        int databaseSizeBeforeCreate = serverRepository.findAll().size();

        // Create the Server

        restServerMockMvc.perform(post("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isCreated());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeCreate + 1);
        Server testServer = serverList.get(serverList.size() - 1);
        assertThat(testServer.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testServer.getUri()).isEqualTo(DEFAULT_URI);
        assertThat(testServer.getResponse()).isEqualTo(DEFAULT_RESPONSE);
        assertThat(testServer.getResponseBlob()).isEqualTo(DEFAULT_RESPONSE_BLOB);
    }

    @Test
    public void createServerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = serverRepository.findAll().size();

        // Create the Server with an existing ID
        Server existingServer = new Server();
        existingServer.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restServerMockMvc.perform(post("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingServer)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = serverRepository.findAll().size();
        // set the field null
        server.setName(null);

        // Create the Server, which fails.

        restServerMockMvc.perform(post("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isBadRequest());

        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkUriIsRequired() throws Exception {
        int databaseSizeBeforeTest = serverRepository.findAll().size();
        // set the field null
        server.setUri(null);

        // Create the Server, which fails.

        restServerMockMvc.perform(post("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isBadRequest());

        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllServers() throws Exception {
        // Initialize the database
        serverRepository.save(server);

        // Get all the serverList
        restServerMockMvc.perform(get("/api/servers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(server.getId())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].uri").value(hasItem(DEFAULT_URI.toString())))
            .andExpect(jsonPath("$.[*].response").value(hasItem(DEFAULT_RESPONSE.toString())))
            .andExpect(jsonPath("$.[*].responseBlob").value(hasItem(DEFAULT_RESPONSE_BLOB.toString())));
    }

    @Test
    public void getServer() throws Exception {
        // Initialize the database
        serverRepository.save(server);

        // Get the server
        restServerMockMvc.perform(get("/api/servers/{id}", server.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(server.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.uri").value(DEFAULT_URI.toString()))
            .andExpect(jsonPath("$.response").value(DEFAULT_RESPONSE.toString()))
            .andExpect(jsonPath("$.responseBlob").value(DEFAULT_RESPONSE_BLOB.toString()));
    }

    @Test
    public void getNonExistingServer() throws Exception {
        // Get the server
        restServerMockMvc.perform(get("/api/servers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateServer() throws Exception {
        // Initialize the database
        serverService.save(server);

        int databaseSizeBeforeUpdate = serverRepository.findAll().size();

        // Update the server
        Server updatedServer = serverRepository.findOne(server.getId());
        updatedServer
                .name(UPDATED_NAME)
                .uri(UPDATED_URI)
                .response(UPDATED_RESPONSE)
                .responseBlob(UPDATED_RESPONSE_BLOB);

        restServerMockMvc.perform(put("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedServer)))
            .andExpect(status().isOk());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeUpdate);
        Server testServer = serverList.get(serverList.size() - 1);
        assertThat(testServer.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testServer.getUri()).isEqualTo(UPDATED_URI);
        assertThat(testServer.getResponse()).isEqualTo(UPDATED_RESPONSE);
        assertThat(testServer.getResponseBlob()).isEqualTo(UPDATED_RESPONSE_BLOB);
    }

    @Test
    public void updateNonExistingServer() throws Exception {
        int databaseSizeBeforeUpdate = serverRepository.findAll().size();

        // Create the Server

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restServerMockMvc.perform(put("/api/servers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(server)))
            .andExpect(status().isCreated());

        // Validate the Server in the database
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteServer() throws Exception {
        // Initialize the database
        serverService.save(server);

        int databaseSizeBeforeDelete = serverRepository.findAll().size();

        // Get the server
        restServerMockMvc.perform(delete("/api/servers/{id}", server.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Server> serverList = serverRepository.findAll();
        assertThat(serverList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
