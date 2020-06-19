package no.unit.nva.customer.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nva.commons.utils.JacocoGenerated;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public class Customer {

    public static final String IDENTIFIER = "identifier";
    public static final String ORG_NUMBER = "feideOrganizationId";


    private UUID identifier;
    private Instant createdDate;
    private Instant modifiedDate;
    private String name;
    private String displayName;
    private String shortName;
    private String archiveName;
    private String cname;
    private String institutionDns;
    private String administrationId;
    private String feideOrganizationId;

    public Customer() {
    }

    private Customer(Builder builder) {
        setIdentifier(builder.identifier);
        setCreatedDate(builder.createdDate);
        setModifiedDate(builder.modifiedDate);
        setName(builder.name);
        setDisplayName(builder.displayName);
        setShortName(builder.shortName);
        setArchiveName(builder.archiveName);
        setCname(builder.cname);
        setInstitutionDns(builder.institutionDns);
        setAdministrationId(builder.administrationId);
        setFeideOrganizationId(builder.feideOrganizationId);
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getInstitutionDns() {
        return institutionDns;
    }

    public void setInstitutionDns(String institutionDns) {
        this.institutionDns = institutionDns;
    }

    public String getAdministrationId() {
        return administrationId;
    }

    public void setAdministrationId(String administrationId) {
        this.administrationId = administrationId;
    }

    public String getFeideOrganizationId() {
        return feideOrganizationId;
    }

    public void setFeideOrganizationId(String feideOrganizationId) {
        this.feideOrganizationId = feideOrganizationId;
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
        Customer that = (Customer) o;
        return Objects.equals(getIdentifier(), that.getIdentifier())
                && Objects.equals(getCreatedDate(), that.getCreatedDate())
                && Objects.equals(getModifiedDate(), that.getModifiedDate())
                && Objects.equals(getName(), that.getName())
                && Objects.equals(getDisplayName(), that.getDisplayName())
                && Objects.equals(getShortName(), that.getShortName())
                && Objects.equals(getArchiveName(), that.getArchiveName())
                && Objects.equals(getCname(), that.getCname())
                && Objects.equals(getInstitutionDns(), that.getInstitutionDns())
                && Objects.equals(getAdministrationId(), that.getAdministrationId())
                && Objects.equals(getFeideOrganizationId(), that.getFeideOrganizationId());
    }

    @Override
    @JacocoGenerated
    public int hashCode() {
        return Objects.hash(getIdentifier(), getCreatedDate(), getModifiedDate(), getName(), getDisplayName(),
                getShortName(), getArchiveName(), getCname(), getInstitutionDns(), getAdministrationId(),
                getFeideOrganizationId());
    }


    public static final class Builder {
        private UUID identifier;
        private Instant createdDate;
        private Instant modifiedDate;
        private String name;
        private String displayName;
        private String shortName;
        private String archiveName;
        private String cname;
        private String institutionDns;
        private String administrationId;
        private String feideOrganizationId;

        public Builder() {
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

        public Builder withArchiveName(String archiveName) {
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

        public Builder withAdministrationId(String administrationId) {
            this.administrationId = administrationId;
            return this;
        }

        public Builder withFeideOrganizationId(String feideOrganizationId) {
            this.feideOrganizationId = feideOrganizationId;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
