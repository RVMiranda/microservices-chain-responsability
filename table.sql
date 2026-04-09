-- 1. Habilitar la extensión para generar UUIDs
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- 2. Crear la tabla en el esquema public
CREATE TABLE public.products_retry_jobs (
    id uuid DEFAULT uuid_generate_v4() NOT NULL,
    product_id varchar NOT NULL,
    request_data jsonb NULL,
    response_data jsonb NULL,
    "action" varchar NOT NULL,
    attempt int4 NOT NULL,
    status varchar NOT NULL,
    next_run_at timestamptz DEFAULT now() NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    updated_at timestamptz DEFAULT now() NOT NULL,
    CONSTRAINT "PK_Id_productsretryjobs" PRIMARY KEY (id)
);
-- 3. Crear los índices
CREATE INDEX idx_retry_jobs_product_id ON public.products_retry_jobs USING btree (product_id);
CREATE INDEX idx_retry_jobs_next_run_at_status ON public.products_retry_jobs USING btree (next_run_at, status) WHERE (status = 'SCHEDULED');
CREATE INDEX idx_retry_jobs_status ON public.products_retry_jobs USING btree (status);
CREATE INDEX idx_retry_jobs_unique_action ON public.products_retry_jobs USING btree (product_id, action);
