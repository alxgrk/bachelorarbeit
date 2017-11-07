package com.alxgrk.bachelorarbeit.accounts;

import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;
import lombok.Setter;

/**
 * The model for a root representation according to
 * {@link com.alxgrk.bachelorarbeit.hateoas.HateoasMediaType#ACCOUNT_TYPE}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Account {

    @Setter(onMethod = @__(@JsonIgnore))
    @JsonProperty("_links")
    private List<Link> links;

    @Setter(onMethod = @__(@JsonIgnore))
    private Integer id;

    private String username;

    private String surname;

    private String name;

    @JsonIgnore
    private String password;

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public void setPassword(String password) {
        this.password = password;
    }
}
