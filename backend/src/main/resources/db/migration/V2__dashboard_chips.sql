CREATE TABLE dashboard_chip (
                                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                code VARCHAR(255) NOT NULL,
                                render_type VARCHAR(50) NOT NULL,
                                form_input_type VARCHAR(50) NOT NULL,
                                title VARCHAR(255) NOT NULL
);

CREATE TABLE dashboard_chip_filters (
                                        dashboard_chip_id BIGINT NOT NULL,
                                        filter VARCHAR(50) NOT NULL,
                                        CONSTRAINT fk_dashboard_chip
                                            FOREIGN KEY (dashboard_chip_id)
                                                REFERENCES dashboard_chip (id)
                                                ON DELETE CASCADE
);

CREATE INDEX idx_dashboard_chip_filters_chip
    ON dashboard_chip_filters (dashboard_chip_id);
