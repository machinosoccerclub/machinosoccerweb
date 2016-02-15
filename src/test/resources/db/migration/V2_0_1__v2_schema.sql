create table login_link_request (
    encoded_date_address varchar primary key,
    hash_key varchar not null,
    email_address varchar not null,
    issued_date date not null
);

create table email (
  email_address varchar primary key,
  receive_activity_schedule boolean not null,
  receive_apply_notification boolean not null,

  status int not null,
  roles varchar not null,

  family_id bigint not null
);

create table member (
  member_no varchar(7) primary key,
  given_name varchar not null,
  family_name varchar not null,
  given_name_kana varchar not null,
  family_name_kana varchar not null,
  grade int not null,
  gender int not null,

  joined_at date not null,

  course int not null,

  family_id bigint not null
);

create table member_photo (
  member_no varchar(7) primary key,
  photo_url varchar not null,
  thumbnail_url varchar not null,
  picasa_photo_entry_id varchar not null,
  picasa_photo_entry_edituri varchar not null
);

create table parent (
  family_id bigserial primary key,

  given_name varchar not null,
  family_name varchar not null,
  given_name_kana varchar not null,
  family_name_kana varchar not null,

  phone_number1 varchar not null,
  phone_number2 varchar
);

CREATE SEQUENCE sq_member_serial;