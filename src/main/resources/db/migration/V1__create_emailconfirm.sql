create table email_conf (
    id serial primary key,
    email varchar not null,
    record_id varchar not null,
    confirmed_at timestamp not null default current_timestamp,
    applied_at timestamp not null default current_timestamp
)