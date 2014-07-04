--liquibase formatted sql

alter table job_scheduler change working_day working_time tinyint(1) null;