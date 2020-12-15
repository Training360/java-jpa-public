create schema if not exists employees default character set utf8 collate utf8_hungarian_ci;

create user 'employees'@'localhost' identified by 'employees';
grant all on employees.* to 'employees'@'localhost';

