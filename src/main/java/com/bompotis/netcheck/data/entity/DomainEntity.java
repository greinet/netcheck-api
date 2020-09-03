/*
 * Copyright 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.bompotis.netcheck.data.entity;

import com.bompotis.netcheck.service.dto.Operation;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.*;

/**
 * Created by Kyriakos Bompotis on 8/6/20.
 */
@Entity
@TypeDefs({
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@Table(name = "domain")
public class DomainEntity extends AbstractTimestampable<String>{

    @Id
    @Column(name = "domain")
    private String domain;

    @NonNull
    @Column(name = "check_frequency_minutes")
    private int checkFrequency;

    @NonNull
    @Column(name = "endpoint")
    private String endpoint;

    @NonNull
    @Column(name = "timeout_ms")
    private int timeoutMs;

    @NonNull
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb", name = "headers")
    private Map<String,String> headers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="domainEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DomainCheckEntity> domainHistoryEntries;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="domainEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DomainMetricEntity> domainMetricEntries;

    protected DomainEntity() {}

    public Set<DomainCheckEntity> getDomainHistoryEntries() {
        return domainHistoryEntries;
    }

    public String getDomain() {
        return domain;
    }

    public int getCheckFrequency() {
        return checkFrequency;
    }

    @Override
    public String getId()  {
        return domain;
    }

    @Override
    public boolean isNew() {
        return null == domain;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Set<DomainMetricEntity> getDomainMetricEntries() {
        return domainMetricEntries;
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public static class Builder {
        private String domain;
        private Set<DomainCheckEntity> domainHistoryEntries;
        private int frequency = 10;
        private String endpoint = "";
        private Map<String,String> headers = new HashMap<>();
        private Set<DomainMetricEntity> domainMetricEntries;
        private int timeoutMs = 30000;
        private Date createdAt = null;

        public Builder fromExistingEntity(DomainEntity entity) {
            this.domain = entity.domain;
            this.domainHistoryEntries = entity.domainHistoryEntries;
            this.frequency = entity.checkFrequency;
            this.endpoint = entity.endpoint;
            this.headers = entity.headers;
            this.domainMetricEntries = entity.domainMetricEntries;
            this.timeoutMs = entity.timeoutMs;
            this.createdAt = entity.getCreatedAt();
            return this;
        }

        public Builder withUpdatedValues(List<Operation> operations) {
            for (var operation : operations) {
                switch (operation.getAction()) {
                    case REMOVE:
                        removeField(operation.getField(), operation.getPath());
                        break;
                    case ADD:
                    case UPDATE:
                        updateField(operation.getField(), operation.getPath(), operation.getValue());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid operation");
                }
            }
            return this;
        }

        private void removeField(String field, String path) {
            switch (field) {
                case "frequency":
                    this.frequency = 10;
                    break;
                case "endpoint":
                    this.endpoint = "";
                    break;
                case "timeout":
                    this.timeoutMs = 30000;
                    break;
                case "headers":
                    this.headers = new HashMap<>();
                    break;
                case "header":
                    this.headers.remove(path);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid property for removal: " + field);
            }
        }

        private void updateField(String field, String path, String value) {
            switch (field) {
                case "frequency":
                    this.frequency = Integer.parseInt(value);
                    break;
                case "endpoint":
                    this.endpoint = value;
                    break;
                case "timeout":
                    this.timeoutMs = Integer.parseInt(value);
                    break;
                case "header":
                    this.headers.put(path,value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid property to update/add: " + field);
            }
        }

        public Builder domainHistoryEntries(Set<DomainCheckEntity> domainHistoryEntries) {
            this.domainHistoryEntries = domainHistoryEntries;
            return this;
        }

        public Builder domainMetricEntries(Set<DomainMetricEntity> domainMetricEntries) {
            this.domainMetricEntries = domainMetricEntries;
            return this;
        }

        public Builder frequency(int frequencyMinutes) {
            this.frequency = frequencyMinutes;
            return this;
        }

        public Builder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder timeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder headers(Map<String,String> headers) {
            if (Optional.ofNullable(headers).isPresent()) {
                this.headers = headers;
            }
            return this;
        }

        public DomainEntity build() {
            return new DomainEntity(this);
        }
    }

    private DomainEntity(Builder b) {
        this.domain = b.domain;
        this.domainHistoryEntries = b.domainHistoryEntries != null ? Set.copyOf(b.domainHistoryEntries) : null;
        this.domainMetricEntries = b.domainMetricEntries != null ? Set.copyOf(b.domainMetricEntries) : null;
        this.checkFrequency = b.frequency;
        this.endpoint = b.endpoint;
        this.headers = Map.copyOf(b.headers);
        this.timeoutMs = b.timeoutMs;
        this.createdAt = b.createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DomainEntity that = (DomainEntity) o;
        return domain.equals(that.domain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain);
    }
}
