package xor.bcmcgroup.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Server.
 */

@Document(collection = "server")
public class Server implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("name")
    private String name;

    @NotNull
    @Field("uri")
    private String uri;

    @Field("response")
    private String response;

    @Field("response_blob")
    private String responseBlob;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Server name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public Server uri(String uri) {
        this.uri = uri;
        return this;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getResponse() {
        return response;
    }

    public Server response(String response) {
        this.response = response;
        return this;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponseBlob() {
        return responseBlob;
    }

    public Server responseBlob(String responseBlob) {
        this.responseBlob = responseBlob;
        return this;
    }

    public void setResponseBlob(String responseBlob) {
        this.responseBlob = responseBlob;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Server server = (Server) o;
        if (server.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, server.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Server{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", uri='" + uri + "'" +
            ", response='" + response + "'" +
            ", responseBlob='" + responseBlob + "'" +
            '}';
    }
}
