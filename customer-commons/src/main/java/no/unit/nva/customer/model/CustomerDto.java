package no.unit.nva.customer.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import no.unit.nva.customer.model.interfaces.Customer;
import no.unit.nva.customer.model.interfaces.JsonLdSupport;
import nva.commons.utils.JacocoGenerated;

@JsonTypeName("Customer")
public class CustomerDto implements Customer, JsonLdSupport {

    private URI id;
    private UUID identifier;
    private Instant createdDate;
    private Instant modifiedDate;
    private String name;
    private String displayName;
    private String shortName;
    private String archiveName;
    private String cname;
    private String institutionDns;
    private String feideOrganizationId;
    private String cristinId;
    @JsonProperty("@context")
    private JsonNode context;

    public CustomerDto() {

    }

    private CustomerDto(Builder builder) {
        setId(builder.id);
        setIdentifier(builder.identifier);
        setCreatedDate(builder.createdDate);
        setModifiedDate(builder.modifiedDate);
        setName(builder.name);
        setDisplayName(builder.displayName);
        setShortName(builder.shortName);
        setArchiveName(builder.archiveName);
        setCname(builder.cname);
        setInstitutionDns(builder.institutionDns);
        setFeideOrganizationId(builder.feideOrganizationId);
        setCristinId(builder.cristinId);
        setContext(builder.context);
    }

    @Override
    public URI getId() {
        return id;
    }

    @Override
    public void setId(URI id) {
        this.id = id;
    }

    @Override
    public UUID getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    @Override
    public Instant getCreatedDate() {
        return createdDate;
    }

    @Override
    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public Instant getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Override
    public String getArchiveName() {
        return archiveName;
    }

    @Override
    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    @Override
    public String getCname() {
        return cname;
    }

    @Override
    public void setCname(String cname) {
        this.cname = cname;
    }

    @Override
    public String getInstitutionDns() {
        return institutionDns;
    }

    @Override
    public void setInstitutionDns(String institutionDns) {
        this.institutionDns = institutionDns;
    }

    @Override
    public String getFeideOrganizationId() {
        return feideOrganizationId;
    }

    @Override
    public void setFeideOrganizationId(String feideOrganizationId) {
        this.feideOrganizationId = feideOrganizationId;
    }

    @Override
    public String getCristinId() {
        return cristinId;
    }

    @Override
    public void setCristinId(String cristinId) {
        this.cristinId = cristinId;
    }

    @Override
    public JsonNode getContext() {
        return context;
    }

    @Override
    public void setContext(JsonNode context) {
        this.context = context;
    }

    @Override
    @JacocoGenerated
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomerDto that = (CustomerDto) o;
        return Objects.equals(getId(), that.getId())
            && Objects.equals(getIdentifier(), that.getIdentifier())
            && Objects.equals(getCreatedDate(), that.getCreatedDate())
            && Objects.equals(getModifiedDate(), that.getModifiedDate())
            && Objects.equals(getName(), that.getName())
            && Objects.equals(getDisplayName(), that.getDisplayName())
            && Objects.equals(getShortName(), that.getShortName())
            && Objects.equals(getArchiveName(), that.getArchiveName())
            && Objects.equals(getCname(), that.getCname())
            && Objects.equals(getInstitutionDns(), that.getInstitutionDns())
            && Objects.equals(getFeideOrganizationId(), that.getFeideOrganizationId())
            && Objects.equals(getCristinId(), that.getCristinId())
            && Objects.equals(getContext(), that.getContext());
    }

    @Override
    @JacocoGenerated
    public int hashCode() {
        return Objects.hash(getId(), getIdentifier(), getCreatedDate(), getModifiedDate(), getName(), getDisplayName(),
            getShortName(), getArchiveName(), getCname(), getInstitutionDns(), getFeideOrganizationId(),
            getCristinId(), getContext());
    }


    public static final class Builder {
        private URI id;
        private UUID identifier;
        private Instant createdDate;
        private Instant modifiedDate;
        private String name;
        private String displayName;
        private String shortName;
        private String archiveName;
        private String cname;
        private String institutionDns;
        private String feideOrganizationId;
        private String cristinId;
        private JsonNode context;

        public Builder() {
        }

        public Builder withId(URI id) {
            this.id = id;
            return this;
        }

        public Builder withIdentifier(UUID identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder withCreatedDate(Instant createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withModifiedDate(Instant modifiedDate) {
            this.modifiedDate = modifiedDate;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder withShortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public CustomerDto.Builder withArchiveName(String archiveName) {
            this.archiveName = archiveName;
            return this;
        }

        public Builder withCname(String cname) {
            this.cname = cname;
            return this;
        }

        public Builder withInstitutionDns(String institutionDns) {
            this.institutionDns = institutionDns;
            return this;
        }

        public Builder withFeideOrganizationId(String feideOrganizationId) {
            this.feideOrganizationId = feideOrganizationId;
            return this;
        }

        public Builder withCristinId(String cristinId) {
            this.cristinId = cristinId;
            return this;
        }

        public Builder withContext(JsonNode context) {
            this.context = context;
            return this;
        }

        public CustomerDto build() {
            return new CustomerDto(this);
        }
    }

}
