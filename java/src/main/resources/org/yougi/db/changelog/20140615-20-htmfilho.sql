--liquibase formatted sql

alter table job_scheduler add end_time time null;