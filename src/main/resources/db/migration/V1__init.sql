create table tenants (
  id uuid primary key,
  name varchar(120) not null,
  created_at timestamptz not null default now()
);
