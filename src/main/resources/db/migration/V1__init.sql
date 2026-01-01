
CREATE TABLE users (
    id UUID NOT NULL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    display_role VARCHAR(255),
    show_on_team BOOLEAN DEFAULT FALSE,
    bio TEXT,
    profile_pic VARCHAR(255),
    reset_password_token VARCHAR(255),
    reset_password_token_expiry TIMESTAMP
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE system_roles (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE product_categories (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE product_registry (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(255),
    category_id UUID,
    price VARCHAR(255),
    description TEXT,
    long_description TEXT,
    support_title VARCHAR(255),
    support_description TEXT,
    support_button_text VARCHAR(255),
    support_button_link VARCHAR(255),
    image_url VARCHAR(255),
    button_text VARCHAR(255),
    button_link VARCHAR(255),
    show_delivery_badge BOOLEAN NOT NULL,
    beta_banner_enabled BOOLEAN NOT NULL,
    beta_banner_message TEXT,
    FOREIGN KEY (category_id) REFERENCES product_categories(id)
);

CREATE TABLE registry_features (
    product_id UUID NOT NULL,
    feature VARCHAR(255),
    FOREIGN KEY (product_id) REFERENCES product_registry(id)
);

CREATE TABLE registry_specs (
    product_id UUID NOT NULL,
    spec_value VARCHAR(255),
    spec_key VARCHAR(255) NOT NULL,
    PRIMARY KEY (product_id, spec_key),
    FOREIGN KEY (product_id) REFERENCES product_registry(id)
);

CREATE TABLE registry_tags (
    product_id UUID NOT NULL,
    tag VARCHAR(255),
    FOREIGN KEY (product_id) REFERENCES product_registry(id)
);

CREATE TABLE registry_highlights (
    product_id UUID NOT NULL,
    icon VARCHAR(255),
    title VARCHAR(255),
    description VARCHAR(255),
    FOREIGN KEY (product_id) REFERENCES product_registry(id)
);

CREATE TABLE registry_faqs (
    product_id UUID NOT NULL,
    question VARCHAR(255),
    answer TEXT,
    FOREIGN KEY (product_id) REFERENCES product_registry(id)
);

CREATE TABLE blog_series (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(2000),
    cover_image VARCHAR(1000),
    creator_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

CREATE TABLE blog_posts (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    excerpt VARCHAR(1000) NOT NULL,
    content TEXT NOT NULL,
    cover_image VARCHAR(1000),
    published BOOLEAN NOT NULL,
    author_id UUID NOT NULL,
    editor_mode VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP,
    view_count BIGINT NOT NULL DEFAULT 0,
    like_count BIGINT NOT NULL DEFAULT 0,
    series_id UUID,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (series_id) REFERENCES blog_series(id)
);

CREATE TABLE blog_tags (
    blog_post_id UUID NOT NULL,
    tag VARCHAR(255),
    FOREIGN KEY (blog_post_id) REFERENCES blog_posts(id)
);

CREATE TABLE comments (
    id UUID NOT NULL PRIMARY KEY,
    content TEXT NOT NULL,
    author_id UUID NOT NULL,
    blog_post_id UUID NOT NULL,
    parent_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (blog_post_id) REFERENCES blog_posts(id),
    FOREIGN KEY (parent_id) REFERENCES comments(id)
);

CREATE TABLE pricing_plans (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price VARCHAR(255) NOT NULL,
    period VARCHAR(255) NOT NULL,
    description TEXT,
    popular BOOLEAN NOT NULL,
    display_order INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE pricing_plan_features (
    plan_id UUID NOT NULL,
    feature VARCHAR(255),
    FOREIGN KEY (plan_id) REFERENCES pricing_plans(id)
);

CREATE TABLE service_offerings (
    id UUID NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    icon VARCHAR(255),
    gradient VARCHAR(255),
    icon_color VARCHAR(255),
    display_order INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE service_features (
    service_id UUID NOT NULL,
    feature VARCHAR(255),
    FOREIGN KEY (service_id) REFERENCES service_offerings(id)
);

CREATE TABLE system_config (
    config_key VARCHAR(255) NOT NULL PRIMARY KEY,
    config_value VARCHAR(255)
);

CREATE TABLE testimonials (
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    rating INTEGER NOT NULL,
    display_order INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
