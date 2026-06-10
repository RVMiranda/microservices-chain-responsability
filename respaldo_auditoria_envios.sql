--
-- PostgreSQL database dump
--

\restrict LQEpImfUfUf7CE9AM7pohQkMV7Yir0D6Orezr7bUNobiYTz4mbkTzYG62TbQ2Up

-- Dumped from database version 15.18
-- Dumped by pg_dump version 15.18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: order_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_history (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    details text,
    order_id character varying(255) NOT NULL,
    status character varying(255) NOT NULL
);


ALTER TABLE public.order_history OWNER TO postgres;

--
-- Name: orders_retry_jobs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orders_retry_jobs (
    id uuid NOT NULL,
    action character varying(255) NOT NULL,
    attempt integer NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    next_run_at timestamp(6) with time zone NOT NULL,
    order_id character varying(255) NOT NULL,
    request_data jsonb,
    response_data jsonb,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL
);


ALTER TABLE public.orders_retry_jobs OWNER TO postgres;

--
-- Name: payment_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payment_history (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    details text,
    payment_id character varying(255) NOT NULL,
    status character varying(255) NOT NULL
);


ALTER TABLE public.payment_history OWNER TO postgres;

--
-- Name: payments_retry_jobs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.payments_retry_jobs (
    id uuid NOT NULL,
    action character varying(255) NOT NULL,
    attempt integer NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    next_run_at timestamp(6) with time zone NOT NULL,
    payment_id character varying(255) NOT NULL,
    request_data jsonb,
    response_data jsonb,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL
);


ALTER TABLE public.payments_retry_jobs OWNER TO postgres;

--
-- Name: product_history; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.product_history (
    id uuid NOT NULL,
    action character varying(255) NOT NULL,
    created_at timestamp(6) with time zone,
    details text,
    product_id character varying(255) NOT NULL
);


ALTER TABLE public.product_history OWNER TO postgres;

--
-- Name: products_retry_jobs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products_retry_jobs (
    id uuid NOT NULL,
    action character varying(255) NOT NULL,
    attempt integer NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    next_run_at timestamp(6) with time zone NOT NULL,
    product_id character varying(255) NOT NULL,
    request_data jsonb,
    response_data jsonb,
    status character varying(255) NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL
);


ALTER TABLE public.products_retry_jobs OWNER TO postgres;

--
-- Name: shipping; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shipping (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    order_id character varying(255) NOT NULL,
    processed_at timestamp(6) with time zone,
    status character varying(255) NOT NULL
);


ALTER TABLE public.shipping OWNER TO postgres;

--
-- Data for Name: order_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.order_history VALUES ('a3a81947-12e7-4dca-8a9e-8ccc3f432b30', '2026-05-06 20:02:35.475214+00', '{"status":"CREADA"}', '69fb9e5be8b47dd92d6a5336', 'SUCCESS');
INSERT INTO public.order_history VALUES ('bd3ff453-f45b-47a6-a097-d7050e87023e', '2026-05-06 22:40:59.974252+00', '{"status":"CREADA"}', '69fbc37b4f644808a19579fe', 'SUCCESS');
INSERT INTO public.order_history VALUES ('c98b6462-b177-4562-b903-36758ad2a727', '2026-05-06 23:16:18.299151+00', '{"status":"CREADA"}', '69fbcbc296f1d699860b074e', 'SUCCESS');
INSERT INTO public.order_history VALUES ('43dcd7d6-6d26-4578-9071-9f1a295ea618', '2026-05-06 23:16:35.795793+00', '{"status":"CANCELADA"}', '69fbcbc296f1d699860b074e', 'SUCCESS');
INSERT INTO public.order_history VALUES ('a52ccaae-8b35-45c2-9948-c15af6962198', '2026-05-08 01:08:12.715241+00', '{"status":"CREADA"}', '69fd377c96f1d699860b074f', 'SUCCESS');
INSERT INTO public.order_history VALUES ('8d71531b-2ea8-41d4-be5b-2b3c7cd67203', '2026-05-08 01:13:34.140106+00', '{"status":"PAGO_PARCIAL"}', '69fd377c96f1d699860b074f', 'SUCCESS');
INSERT INTO public.order_history VALUES ('8f6a0e59-7e97-4064-ac3c-c740a3e1ee6e', '2026-05-08 01:49:26.016842+00', '{"status":"PAGADA"}', '69fd377c96f1d699860b074f', 'SUCCESS');
INSERT INTO public.order_history VALUES ('3fc1dfb2-728d-425f-ab6b-3f76f4960b39', '2026-05-08 01:53:16.982346+00', '{"status":"CREADA"}', '69fd420c96f1d699860b0750', 'SUCCESS');
INSERT INTO public.order_history VALUES ('df1a90fe-4e16-4d4f-b858-a5be6c3b07c4', '2026-05-08 01:54:32.109307+00', '{"status":"PAGO_PARCIAL"}', '69fd420c96f1d699860b0750', 'SUCCESS');
INSERT INTO public.order_history VALUES ('c78a5fac-27ff-474e-90d7-1551ec5308f3', '2026-05-08 01:56:08.917047+00', '{"status":"PAGADA"}', '69fd420c96f1d699860b0750', 'SUCCESS');
INSERT INTO public.order_history VALUES ('72e3b761-1d0a-4811-bc59-44e2e621b0fc', '2026-05-08 01:57:51.231254+00', '{"status":"CREADA"}', '69fd431f96f1d699860b0751', 'SUCCESS');
INSERT INTO public.order_history VALUES ('ac2aff14-629b-45dc-9b8c-21fb7530cf7d', '2026-05-08 01:58:51.654261+00', '{"status":"PAGADA"}', '69fd431f96f1d699860b0751', 'SUCCESS');
INSERT INTO public.order_history VALUES ('216c0c64-16a4-4345-94f2-8a798d722a79', '2026-06-07 00:28:13.523989+00', '{"status":"CREADA"}', '6a24bb1d2c600b0327dedb05', 'SUCCESS');
INSERT INTO public.order_history VALUES ('17f01104-3d74-4b2f-bd74-ec42b94e1cdd', '2026-06-07 00:36:59.05836+00', '{"status":"PAGO_PARCIAL"}', '6a24bb1d2c600b0327dedb05', 'SUCCESS');
INSERT INTO public.order_history VALUES ('d76fd1aa-2be6-4c93-ab54-4f9bca18077f', '2026-06-07 00:38:38.46196+00', '{"status":"PAGO_PARCIAL"}', '6a24bb1d2c600b0327dedb05', 'SUCCESS');
INSERT INTO public.order_history VALUES ('71f86a14-2b8e-4427-98ee-05a620818333', '2026-06-07 00:39:48.268022+00', '{"status":"PAGADA"}', '6a24bb1d2c600b0327dedb05', 'SUCCESS');
INSERT INTO public.order_history VALUES ('1518dff2-ce56-4ba6-9a57-e879e8f9e389', '2026-06-10 01:40:02.387728+00', '{"status":"CREADA"}', '6a28c0728537da8aa3dfd0da', 'SUCCESS');
INSERT INTO public.order_history VALUES ('db326353-aaa2-4bcf-895d-d8a7a1592cb7', '2026-06-10 01:41:07.464395+00', '{"status":"PAGO_PARCIAL"}', '6a28c0728537da8aa3dfd0da', 'SUCCESS');
INSERT INTO public.order_history VALUES ('c89c489e-e05d-4050-bb48-222b0f09dc01', '2026-06-10 01:41:45.507086+00', '{"status":"PAGADA"}', '6a28c0728537da8aa3dfd0da', 'SUCCESS');
INSERT INTO public.order_history VALUES ('da98796a-5640-44ca-909f-c00949361dae', '2026-06-10 06:19:21.35467+00', '{"status":"CREADA"}', '6a2901e900669002ba6f3a76', 'SUCCESS');
INSERT INTO public.order_history VALUES ('7600de40-563f-4fce-bfea-755170f8baf4', '2026-06-10 06:20:12.178034+00', '{"status":"PAGO_PARCIAL"}', '6a2901e900669002ba6f3a76', 'SUCCESS');
INSERT INTO public.order_history VALUES ('dfc21589-c41f-41f7-9430-d6d618006762', '2026-06-10 06:20:46.407485+00', '{"status":"PAGO_PARCIAL"}', '6a2901e900669002ba6f3a76', 'SUCCESS');
INSERT INTO public.order_history VALUES ('9d5baa45-8d6d-44d5-862e-22f4194f795b', '2026-06-10 06:21:00.601801+00', '{"status":"PAGADA"}', '6a2901e900669002ba6f3a76', 'SUCCESS');
INSERT INTO public.order_history VALUES ('fa4af5c8-dee4-47aa-bcd3-b716cdd82947', '2026-06-10 06:27:15.212608+00', '{"status":"CANCELADA"}', '69fd431f96f1d699860b0751', 'SUCCESS');
INSERT INTO public.order_history VALUES ('60230d05-3e2d-43ef-9189-a9c9a8a377b2', '2026-06-10 06:28:23.37927+00', '{"status":"REEMBOLSADA"}', '69fbc37b4f644808a19579fe', 'SUCCESS');
INSERT INTO public.order_history VALUES ('da7e02ef-24f9-4722-977a-23a377a40b14', '2026-06-10 06:28:58.976503+00', '{"status":"REEMBOLSADA"}', '69fbc37b4f644808a19579fe', 'SUCCESS');
INSERT INTO public.order_history VALUES ('d2b563c3-573e-4344-adcf-79e61be399f2', '2026-06-10 06:30:08.565619+00', '{"status":"REEMBOLSADA"}', '69fb9e5be8b47dd92d6a5336', 'SUCCESS');
INSERT INTO public.order_history VALUES ('6453d7ca-fd49-45f1-93f2-16cbb7b0a163', '2026-06-10 06:30:15.622312+00', '{"status":"REEMBOLSADA"}', '69fb9e5be8b47dd92d6a5336', 'SUCCESS');
INSERT INTO public.order_history VALUES ('0d217d26-fae3-412d-b7a5-383660bf4d74', '2026-06-10 06:30:56.250494+00', '{"status":"CANCELADA"}', '69fb9e5be8b47dd92d6a5336', 'SUCCESS');


--
-- Data for Name: orders_retry_jobs; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.orders_retry_jobs VALUES ('c96aa8c3-ab69-4ee3-8bbd-72bb6185aefd', 'UPDATE', 1, '2026-06-10 07:08:02.45425+00', '2026-06-10 07:13:02.426534+00', '69b109b389bb6e9cf69337f8', '{"status": "CREADA"}', NULL, 'SUCCESS', '2026-06-10 07:13:07.547456+00');
INSERT INTO public.orders_retry_jobs VALUES ('3f7e3fcf-7705-45b9-af76-319ed4f0a755', 'UPDATE', 1, '2026-06-10 07:08:12.695377+00', '2026-06-10 07:13:12.694082+00', '69b109b389bb6e9cf69337f8', '{"status": "CREADA"}', NULL, 'SUCCESS', '2026-06-10 07:13:17.579571+00');
INSERT INTO public.orders_retry_jobs VALUES ('ea2ef19b-f0f8-4f63-9f21-1f725dd147d0', 'UPDATE', 1, '2026-06-10 07:08:19.972462+00', '2026-06-10 07:13:19.971163+00', '69b109b389bf6e9cf69337f8', '{"status": "CREADA"}', NULL, 'SUCCESS', '2026-06-10 07:13:27.593778+00');
INSERT INTO public.orders_retry_jobs VALUES ('1edd4de6-d43d-4666-bcb1-3360d8a09547', 'CREATE', 1, '2026-06-10 07:09:37.395897+00', '2026-06-10 07:14:37.392531+00', 'unknown-order-id', '{"quantity": 2, "productId": "69fd41e1f4fad8ced2ff33b0", "userEmail": "TheRafa@modelo.com"}', NULL, 'SUCCESS', '2026-06-10 07:14:37.637005+00');
INSERT INTO public.orders_retry_jobs VALUES ('6ed742cb-b23c-404d-b1a8-d35a9e8a10e6', 'CREATE', 1, '2026-06-10 07:09:48.225652+00', '2026-06-10 07:14:48.224486+00', 'unknown-order-id', '{"quantity": 2, "productId": "69fd41e1f4fad8ced2ff33b0", "userEmail": "TheRafa@modelo.com"}', NULL, 'SUCCESS', '2026-06-10 07:14:57.652888+00');


--
-- Data for Name: payment_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.payment_history VALUES ('18287789-9bb7-4e46-81dc-614dc346bd75', '2026-05-06 20:03:28.731016+00', '{"orderId":"69fb9e5be8b47dd92d6a5336", "paymentMethod":"TARJETA", "amount":5000.0, "userEmail":"null", "isFullyPaid":false}', '69fb9e90ae93c012f6180363', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('981fc892-bad1-4e03-9ef6-1df04b3d213e', '2026-05-06 20:07:48.432994+00', '{"orderId":"69fb9e5be8b47dd92d6a5336", "paymentMethod":"TARJETA", "amount":980.0, "userEmail":"null", "isFullyPaid":true}', '69fb9f94ae93c012f6180364', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('d0e0e991-ade0-41a4-be69-2433111b546f', '2026-05-06 22:43:49.49253+00', '{"orderId":"69fbc37b4f644808a19579fe", "paymentMethod":"TARJETA", "amount":10000.0, "userEmail":"null", "isFullyPaid":false}', '69fbc4256ffedd11b40413d6', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('efb20720-f0fe-4705-9b20-95d5cd17c866', '2026-05-06 22:44:24.301516+00', '{"orderId":"69fbc37b4f644808a19579fe", "paymentMethod":"TARJETA", "amount":1500.0, "userEmail":"null", "isFullyPaid":true}', '69fbc4486ffedd11b40413d7', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('c4df1ff1-aa69-4839-a915-fa7d164560aa', '2026-05-08 01:13:34.079661+00', '{"orderId":"69fd377c96f1d699860b074f", "paymentMethod":"TARJETA", "amount":20000.0, "userEmail":"null", "isFullyPaid":false}', '69fd38bd9ababd8e21c62724', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('723da659-4293-4996-9c93-fd9baf0659fe', '2026-05-08 01:49:25.97686+00', '{"orderId":"69fd377c96f1d699860b074f", "paymentMethod":"TARJETA", "amount":3000.0, "userEmail":"null", "isFullyPaid":true}', '69fd41259ababd8e21c62725', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('1b4c8e66-7811-4034-b487-fe7b00674cab', '2026-05-08 01:54:32.074333+00', '{"orderId":"69fd420c96f1d699860b0750", "paymentMethod":"TARJETA", "amount":3000.0, "userEmail":"null", "isFullyPaid":false}', '69fd42589ababd8e21c62726', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('a896aeb1-e129-4a50-9406-4fa1b02c2b09', '2026-05-08 01:56:08.890895+00', '{"orderId":"69fd420c96f1d699860b0750", "paymentMethod":"TARJETA", "amount":200.0, "userEmail":"null", "isFullyPaid":true}', '69fd42b89ababd8e21c62727', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('4f993c89-128f-4995-bab0-782bc904655a', '2026-06-07 00:36:58.998697+00', '{"orderId":"6a24bb1d2c600b0327dedb05", "paymentMethod":"TARJETA", "amount":4000.0, "userEmail":"null", "isFullyPaid":false, "remainingBalance":2400.0}', '6a24bd2a87c3fe6800000608', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('a29877c3-48fa-4acc-bace-5c377683a2b6', '2026-06-07 00:38:38.414623+00', '{"orderId":"6a24bb1d2c600b0327dedb05", "paymentMethod":"TARJETA", "amount":2000.0, "userEmail":"null", "isFullyPaid":false, "remainingBalance":400.0}', '6a24bd8e87c3fe6800000609', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('7685d213-7136-4817-acec-634c3fb144de', '2026-06-07 00:39:48.239144+00', '{"orderId":"6a24bb1d2c600b0327dedb05", "paymentMethod":"TARJETA", "amount":400.0, "userEmail":"null", "isFullyPaid":true, "remainingBalance":0.0}', '6a24bdd487c3fe680000060a', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('0b6f3da8-686b-4d37-903f-de6259a641d3', '2026-06-10 01:41:07.415124+00', '{"orderId":"6a28c0728537da8aa3dfd0da", "paymentMethod":"CREDIT_CARD", "amount":600.0, "userEmail":"eduardo@gmail.com", "isFullyPaid":false, "remainingBalance":199.0}', '6a28c0b3593258bc2ca6fa48', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('ac208039-7424-4e4d-bdb7-b7bbb4d6290c', '2026-06-10 01:41:45.487968+00', '{"orderId":"6a28c0728537da8aa3dfd0da", "paymentMethod":"CREDIT_CARD", "amount":199.0, "userEmail":"eduardo@gmail.com", "isFullyPaid":true, "remainingBalance":0.0}', '6a28c0d9593258bc2ca6fa49', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('aa173460-a494-42e7-b7a3-5579f9665c5c', '2026-06-10 06:20:12.100859+00', '{"orderId":"6a2901e900669002ba6f3a76", "paymentMethod":"PAYPAL", "amount":1300.0, "userEmail":"Rafa@gmail.com", "isFullyPaid":false, "remainingBalance":298.0}', '6a29021baa2d03cf9b74b3d1', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('e7f3a4b3-9709-4ff2-8714-8e725d206eb3', '2026-06-10 06:20:46.384272+00', '{"orderId":"6a2901e900669002ba6f3a76", "paymentMethod":"PAYPAL", "amount":200.0, "userEmail":"Rafa@gmail.com", "isFullyPaid":false, "remainingBalance":98.0}', '6a29023eaa2d03cf9b74b3d2', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('b85ad9e5-063a-4248-8627-cc777a61f6ba', '2026-06-10 06:21:00.561364+00', '{"orderId":"6a2901e900669002ba6f3a76", "paymentMethod":"DEBIT_CARD", "amount":98.0, "userEmail":"Rafa@gmail.com", "isFullyPaid":true, "remainingBalance":0.0}', '6a29024caa2d03cf9b74b3d3', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('51131d8b-39bf-4c1a-a94f-56efbf24f0d6', '2026-06-10 06:28:23.346079+00', '{"orderId":"69fbc37b4f644808a19579fe", "paymentMethod":"TARJETA", "amount":1500.0, "userEmail":"null", "isFullyPaid":false, "remainingBalance":1500.0}', '69fbc4486ffedd11b40413d7', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('4cc88ba4-85ef-437d-9cbd-dd0f01e7c2e7', '2026-06-10 06:28:58.95137+00', '{"orderId":"69fbc37b4f644808a19579fe", "paymentMethod":"TARJETA", "amount":10000.0, "userEmail":"null", "isFullyPaid":false, "remainingBalance":11500.0}', '69fbc4256ffedd11b40413d6', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('183fbaa1-a4f8-4e32-afb0-db2313bd71a7', '2026-06-10 06:30:08.547896+00', '{"orderId":"69fb9e5be8b47dd92d6a5336", "paymentMethod":"TARJETA", "amount":5000.0, "userEmail":"null", "isFullyPaid":false, "remainingBalance":5000.0}', '69fb9e90ae93c012f6180363', 'SUCCESS');
INSERT INTO public.payment_history VALUES ('e626785e-65a8-4ac8-a5b6-8f139533267e', '2026-06-10 06:30:15.599247+00', '{"orderId":"69fb9e5be8b47dd92d6a5336", "paymentMethod":"TARJETA", "amount":980.0, "userEmail":"null", "isFullyPaid":false, "remainingBalance":5980.0}', '69fb9f94ae93c012f6180364', 'SUCCESS');


--
-- Data for Name: payments_retry_jobs; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.payments_retry_jobs VALUES ('a33de179-8a68-49f1-9563-aae78c35c779', 'PROCESS', 1, '2026-05-06 20:07:03.281418+00', '2026-05-06 20:12:03.238857+00', 'unknown-payment-id', '{"orderId": "69fb9e5be8b47dd92d6a5336", "paymentMethod": "TARJETA"}', NULL, 'SUCCESS', '2026-05-06 20:12:08.513694+00');
INSERT INTO public.payments_retry_jobs VALUES ('c8d00048-c9fa-4372-b290-5e2b3b0143e9', 'PROCESS', 1, '2026-05-06 23:16:59.334429+00', '2026-05-06 23:21:59.321006+00', 'unknown-payment-id', '{"orderId": "69fbcbc296f1d699860b074e", "paymentMethod": "TARJETA"}', NULL, 'SUCCESS', '2026-05-06 23:53:38.810001+00');
INSERT INTO public.payments_retry_jobs VALUES ('767eb6ce-7115-44cc-a392-e8a4e499af24', 'PROCESS', 1, '2026-05-08 01:55:36.780849+00', '2026-05-08 02:00:36.73045+00', 'unknown-payment-id', '{"orderId": "69fd420c96f1d699860b0750", "paymentMethod": "TARJETA"}', NULL, 'SUCCESS', '2026-05-08 02:00:41.578729+00');
INSERT INTO public.payments_retry_jobs VALUES ('69e0be92-9352-4f18-bd6a-e5c9ebfa793a', 'PROCESS', 1, '2026-06-07 00:34:02.931538+00', '2026-06-07 00:39:02.909757+00', 'unknown-payment-id', '{"orderId": "69e18548995b227870dda7d9", "paymentMethod": "TARJETA"}', NULL, 'SUCCESS', '2026-06-07 00:39:08.589306+00');
INSERT INTO public.payments_retry_jobs VALUES ('42eb8023-5b25-4970-b0c5-164524e44f3d', 'PROCESS', 1, '2026-06-07 00:39:42.722313+00', '2026-06-07 00:44:42.719604+00', 'unknown-payment-id', '{"orderId": "6a24bb1d2c600b0327dedb05", "paymentMethod": "TARJETA"}', NULL, 'SUCCESS', '2026-06-07 00:44:48.795116+00');


--
-- Data for Name: product_history; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.product_history VALUES ('29702883-3e7a-493b-bf52-670fa3fbd3a7', 'CREATE', '2026-05-06 20:02:04.032135+00', '{"name":"Teclado Mecánico Logitech G Pro X", "description":"Teclado gamer mecanico 60%", "price":2990.0, "stock":20}', '69fb9e3b26777ff825c775ed');
INSERT INTO public.product_history VALUES ('f3b89b36-0572-46fe-a7d1-6e931343c6ce', 'UPDATE', '2026-05-06 21:58:53.668283+00', '{"name":"PlayStation 5 Pro", "description":"Consola Digital de Videojuegos", "price":11500.0, "stock":18}', '69fb9e3b26777ff825c775ed');
INSERT INTO public.product_history VALUES ('7d86d4ca-8184-4392-aeb7-a5e1a0cc7dec', 'CREATE', '2026-05-08 01:52:33.933959+00', '{"name":"Teclado Mecánico Hyper X", "description":"Teclado gamer mecanico 60%", "price":3200.0, "stock":20}', '69fd41e1f4fad8ced2ff33ba');
INSERT INTO public.product_history VALUES ('4cb8173f-6510-4b11-ae1c-395c60291a4f', 'CREATE_ORDER_INVENTORY', '2026-05-08 01:53:17.001333+00', '{"productId":"69fd41e1f4fad8ced2ff33ba", "quantity":1}', '69fd41e1f4fad8ced2ff33ba');
INSERT INTO public.product_history VALUES ('d344bb0c-6d45-4f7a-9006-a28871010ec4', 'CREATE_ORDER_INVENTORY', '2026-05-08 01:57:51.231045+00', '{"productId":"69fd41e1f4fad8ced2ff33ba", "quantity":1}', '69fd41e1f4fad8ced2ff33ba');
INSERT INTO public.product_history VALUES ('8fb0a20e-3bf8-4c46-ac7a-4b3d69c2aed4', 'CREATE_ORDER_INVENTORY', '2026-06-07 00:28:13.534701+00', '{"productId":"69fd41e1f4fad8ced2ff33ba", "quantity":2}', '69fd41e1f4fad8ced2ff33ba');
INSERT INTO public.product_history VALUES ('51ca5f95-2215-49a4-91f3-e8622ec72943', 'CREATE', '2026-06-09 19:46:55.76642+00', '{"name":"Control Gamesir 7", "description":"Control alambrico gamesir 7, compatible con xbox, pc, nintendo, android y ios.", "price":799.0, "stock":12}', '6a286daf183808bb0773363a');
INSERT INTO public.product_history VALUES ('bb3e98fe-c0a1-410f-88d9-ad4854283be7', 'CREATE', '2026-06-09 19:49:45.399161+00', '{"name":"Game", "description":"prueba de eliminar", "price":199.0, "stock":11}', '6a286e59183808bb0773363b');
INSERT INTO public.product_history VALUES ('5393cd3d-9b1d-4ba6-bd3c-88788daa0143', 'UPDATE', '2026-06-09 22:05:01.893767+00', '{"name":"Control Gamesir 7", "description":"Control alambrico anti drift, gamesir 7, compatible con xbox, pc, nintendo, android y ios.", "price":799.0, "stock":12}', '6a286daf183808bb0773363a');
INSERT INTO public.product_history VALUES ('896d4fa3-df3a-47d5-91c0-95a1fb2fe026', 'CREATE_ORDER_INVENTORY', '2026-06-10 01:40:02.412372+00', '{"productId":"6a286daf183808bb0773363a", "quantity":1}', '6a286daf183808bb0773363a');
INSERT INTO public.product_history VALUES ('f7044f18-148f-4ce5-818f-7a9ee60dfdd2', 'CREATE_ORDER_INVENTORY', '2026-06-10 06:19:21.344341+00', '{"productId":"6a286daf183808bb0773363a", "quantity":2}', '6a286daf183808bb0773363a');
INSERT INTO public.product_history VALUES ('bbe9075c-b058-4374-a37e-6948b1d07468', 'RESTORE_ORDER_INVENTORY', '2026-06-10 06:27:15.212177+00', '{"productId":"69fd41e1f4fad8ced2ff33ba", "quantity":1}', '69fd41e1f4fad8ced2ff33ba');
INSERT INTO public.product_history VALUES ('9722e07d-beb5-4f60-9973-0cf454c0bfd8', 'RESTORE_ORDER_INVENTORY', '2026-06-10 06:30:56.257288+00', '{"productId":"69fb9e3b26777ff825c775ed", "quantity":2}', '69fb9e3b26777ff825c775ed');
INSERT INTO public.product_history VALUES ('60a1db3d-29ba-4716-ae35-64e9febe126d', 'CREATE', '2026-06-10 07:12:25.286563+00', '{"name":"Teclado Mecánico Logitech G Pro X", "description":"Teclado gamer mecanico 60%", "price":2990.0, "stock":null}', '6a290e59133d61842fd93624');
INSERT INTO public.product_history VALUES ('6eefed31-bcf8-4450-bf4c-45e7f55571c8', 'CREATE', '2026-06-10 07:12:45.563836+00', '{"name":"Teclado Mecánico Logitech G Pro X", "description":"Teclado gamer mecanico 60%", "price":2990.0, "stock":0}', '6a290e6d133d61842fd93625');
INSERT INTO public.product_history VALUES ('b74b5129-12d3-42c6-bce8-807abb5fec3b', 'CREATE', '2026-06-10 07:12:51.620751+00', '{"name":"Teclado Mecánico Logitech G Pro X", "description":"Teclado gamer mecanico 60%", "price":2990.0, "stock":-2}', '6a290e73133d61842fd93626');


--
-- Data for Name: products_retry_jobs; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.products_retry_jobs VALUES ('2da58156-e6bb-4b59-98be-c5f40d08235c', 'CREATE', 0, '2026-06-10 07:13:00.123429+00', '2026-06-10 07:18:00.123315+00', 'fake-test-product-id', '{"name": "Test Product"}', NULL, 'SCHEDULED', '2026-06-10 07:13:00.123431+00');
INSERT INTO public.products_retry_jobs VALUES ('58a75a84-d3a0-40d7-ac5c-ac09f55cce4c', 'CREATE', 0, '2026-06-10 07:13:02.909434+00', '2026-06-10 07:18:02.909414+00', 'fake-test-product-id', '{"name": "Test Product"}', NULL, 'SCHEDULED', '2026-06-10 07:13:02.909435+00');
INSERT INTO public.products_retry_jobs VALUES ('c19165d1-276a-423b-b2f1-7d0a64e5b2e9', 'CREATE', 0, '2026-06-10 07:13:04.025513+00', '2026-06-10 07:18:04.0255+00', 'fake-test-product-id', '{"name": "Test Product"}', NULL, 'SCHEDULED', '2026-06-10 07:13:04.025514+00');
INSERT INTO public.products_retry_jobs VALUES ('e5d4456e-ed84-4a73-b735-f3d1971f89d4', 'CREATE', 0, '2026-06-10 07:13:05.505866+00', '2026-06-10 07:18:05.505849+00', 'fake-test-product-id', '{"name": "Test Product"}', NULL, 'SCHEDULED', '2026-06-10 07:13:05.505867+00');
INSERT INTO public.products_retry_jobs VALUES ('a203de56-dc10-42a0-a9af-94389c287a1e', 'CREATE', 0, '2026-06-10 07:13:06.454804+00', '2026-06-10 07:18:06.454739+00', 'fake-test-product-id', '{"name": "Test Product"}', NULL, 'SCHEDULED', '2026-06-10 07:13:06.454804+00');


--
-- Data for Name: shipping; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO public.shipping VALUES ('aca4820a-e8c7-40d2-9f42-218afded2ac2', '2026-05-06 20:07:48.470639+00', '69fb9e5be8b47dd92d6a5336', '2026-05-06 20:07:58.242116+00', 'SHIPPED');
INSERT INTO public.shipping VALUES ('c62096c0-6f2f-4e0d-97a0-e632dbd6c53a', '2026-05-06 22:44:24.318334+00', '69fbc37b4f644808a19579fe', '2026-05-06 22:44:34.267908+00', 'SHIPPED');
INSERT INTO public.shipping VALUES ('7d844b17-21c8-4385-9cdb-a323d9216ad8', '2026-05-08 01:49:26.02421+00', '69fd377c96f1d699860b074f', '2026-05-08 01:49:30.813632+00', 'SHIPPED');
INSERT INTO public.shipping VALUES ('645dc0db-e01b-4899-a5c5-6a8557262740', '2026-05-08 01:56:08.905495+00', '69fd420c96f1d699860b0750', '2026-05-08 01:56:11.250533+00', 'SHIPPED');
INSERT INTO public.shipping VALUES ('3043a79a-4bf7-4067-932b-68ff05e4c3c3', '2026-05-08 01:58:51.670578+00', '69fd431f96f1d699860b0751', '2026-05-08 01:59:01.445401+00', 'SHIPPED');
INSERT INTO public.shipping VALUES ('e4496164-f29d-41cc-b444-282bc104e838', '2026-06-07 00:39:48.252172+00', '6a24bb1d2c600b0327dedb05', '2026-06-10 01:57:14.487356+00', 'SHIPPED');
INSERT INTO public.shipping VALUES ('2bc93621-b03f-4f92-ab8f-98cedd8a7ddb', '2026-06-10 01:41:45.498658+00', '6a28c0728537da8aa3dfd0da', '2026-06-10 01:57:14.490879+00', 'SHIPPED');
INSERT INTO public.shipping VALUES ('d415b124-69c5-46c3-b828-923440f1b44d', '2026-06-10 06:21:00.569236+00', '6a2901e900669002ba6f3a76', '2026-06-10 06:21:05.161676+00', 'SHIPPED');


--
-- Name: order_history order_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_history
    ADD CONSTRAINT order_history_pkey PRIMARY KEY (id);


--
-- Name: orders_retry_jobs orders_retry_jobs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders_retry_jobs
    ADD CONSTRAINT orders_retry_jobs_pkey PRIMARY KEY (id);


--
-- Name: payment_history payment_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payment_history
    ADD CONSTRAINT payment_history_pkey PRIMARY KEY (id);


--
-- Name: payments_retry_jobs payments_retry_jobs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.payments_retry_jobs
    ADD CONSTRAINT payments_retry_jobs_pkey PRIMARY KEY (id);


--
-- Name: product_history product_history_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.product_history
    ADD CONSTRAINT product_history_pkey PRIMARY KEY (id);


--
-- Name: products_retry_jobs products_retry_jobs_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products_retry_jobs
    ADD CONSTRAINT products_retry_jobs_pkey PRIMARY KEY (id);


--
-- Name: shipping shipping_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipping
    ADD CONSTRAINT shipping_pkey PRIMARY KEY (id);


--
-- Name: shipping uk5tqgqx54jwxmib2c1f49l677a; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shipping
    ADD CONSTRAINT uk5tqgqx54jwxmib2c1f49l677a UNIQUE (order_id);


--
-- PostgreSQL database dump complete
--

\unrestrict LQEpImfUfUf7CE9AM7pohQkMV7Yir0D6Orezr7bUNobiYTz4mbkTzYG62TbQ2Up

