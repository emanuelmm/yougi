--liquibase formatted sql

alter table mailing_list_message add date_sent timestamp null;