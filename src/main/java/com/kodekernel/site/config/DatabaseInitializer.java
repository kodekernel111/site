package com.kodekernel.site.config;

import com.kodekernel.site.entity.PricingPlan;
import com.kodekernel.site.entity.ServiceOffering;
import com.kodekernel.site.entity.Testimonial;
import com.kodekernel.site.repository.PricingPlanRepository;
import com.kodekernel.site.repository.ServiceOfferingRepository;
import com.kodekernel.site.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PricingPlanRepository pricingPlanRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final TestimonialRepository testimonialRepository;
    private final com.kodekernel.site.repository.SystemRoleRepository systemRoleRepository;
    private final com.kodekernel.site.repository.ProductCategoryRepository productCategoryRepository;
    private final com.kodekernel.site.repository.ProductRepository productRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Running Database Initializer...");
        
        // Create comments table
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS comments (
                id UUID NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP,
                updated_at TIMESTAMP,
                author_id UUID NOT NULL,
                blog_post_id UUID NOT NULL,
                CONSTRAINT pk_comments PRIMARY KEY (id),
                CONSTRAINT fk_comments_users FOREIGN KEY (author_id) REFERENCES users (id),
                CONSTRAINT fk_comments_blog_posts FOREIGN KEY (blog_post_id) REFERENCES blog_posts (id)
            );
        """);

        // Add columns to blog_posts
        try {
            jdbcTemplate.execute("ALTER TABLE blog_posts ADD COLUMN IF NOT EXISTS view_count BIGINT DEFAULT 0 NOT NULL");
            jdbcTemplate.execute("ALTER TABLE blog_posts ADD COLUMN IF NOT EXISTS like_count BIGINT DEFAULT 0 NOT NULL");
        } catch (Exception e) {
            System.out.println("Error adding columns to blog_posts: " + e.getMessage());
        }

        // Fix nulls for new user columns to prevent Hibernate errors
        try {
             jdbcTemplate.execute("UPDATE users SET show_on_team = false WHERE show_on_team IS NULL");
             System.out.println("Fixed null show_on_team values.");
        } catch (Exception e) {
             System.out.println("Error fixing null values: " + e.getMessage());
        }

         // Ensure Product table has new columns
         try {
             jdbcTemplate.execute("ALTER TABLE product_registry ADD COLUMN IF NOT EXISTS show_delivery_badge BOOLEAN DEFAULT true NOT NULL");
             jdbcTemplate.execute("ALTER TABLE product_registry ADD COLUMN IF NOT EXISTS beta_banner_enabled BOOLEAN DEFAULT false NOT NULL");
             jdbcTemplate.execute("ALTER TABLE product_registry ADD COLUMN IF NOT EXISTS beta_banner_message TEXT");
             System.out.println("Verified product_registry schema evolution.");
         } catch (Exception e) {
             System.out.println("Error evolving product_registry: " + e.getMessage());
         }

        // Seed System Roles
        if (systemRoleRepository.count() == 0) {
            systemRoleRepository.save(com.kodekernel.site.entity.SystemRole.builder().name("ADMIN").build());
            systemRoleRepository.save(com.kodekernel.site.entity.SystemRole.builder().name("USER").build());
            systemRoleRepository.save(com.kodekernel.site.entity.SystemRole.builder().name("WRITER").build());
            System.out.println("Seeded system roles.");
        }

        // Seed Pricing Plans
        if (pricingPlanRepository.count() == 0) {
            System.out.println("Seeding default pricing plans...");
            PricingPlan starter = PricingPlan.builder().name("Starter").price("$999").period("per project").description("Perfect for small businesses.").features(List.of("Custom Website", "Mobile Layout")).popular(false).order(1).build();
            PricingPlan professional = PricingPlan.builder().name("Professional").price("$2,499").period("per project").description("Ideal for growing businesses.").features(List.of("Everything in Starter", "CMS Integration")).popular(true).order(2).build();
            PricingPlan enterprise = PricingPlan.builder().name("Enterprise").price("Custom").period("contact us").description("Tailored solutions.").features(List.of("Everything in Professional", "Custom Dev")).popular(false).order(3).build();
            pricingPlanRepository.saveAll(Arrays.asList(starter, professional, enterprise));
            System.out.println("Pricing plans seeded.");
        }

        // Seed Services
        if (serviceOfferingRepository.count() == 0) {
            System.out.println("Seeding default services...");
             ServiceOffering webDev = ServiceOffering.builder().title("Web Development").description("We build fast, scalable web apps.").features(List.of("React", "Next.js")).icon("Code").gradient("from-blue-500/20 to-cyan-500/20").iconColor("text-cyan-500").displayOrder(1).build();
             ServiceOffering uiUx = ServiceOffering.builder().title("UI/UX Design").description("Beautiful, intuitive interfaces.").features(List.of("Figma", "Prototyping")).icon("Palette").gradient("from-purple-500/20 to-pink-500/20").iconColor("text-pink-500").displayOrder(2).build();
             // (Shortened for brevity, logic remains)
             serviceOfferingRepository.saveAll(Arrays.asList(webDev, uiUx)); // Re-seed if empty.
             System.out.println("Services seeded.");
        }

        // Seed Testimonials
        if (testimonialRepository.count() == 0) {
            System.out.println("Seeding default testimonials...");
            // (Shortened)
            Testimonial t1 = Testimonial.builder().name("Sarah Johnson").role("CEO").content("Great work!").rating(5).displayOrder(1).build();
            testimonialRepository.save(t1);
            System.out.println("Testimonials seeded.");
        }

        // Seed Product Categories
        if (productCategoryRepository.count() == 0) {
            System.out.println("Seeding default product categories...");
            productCategoryRepository.save(com.kodekernel.site.entity.ProductCategory.builder().name("SaaS").build());
            productCategoryRepository.save(com.kodekernel.site.entity.ProductCategory.builder().name("Mobile App").build());
            productCategoryRepository.save(com.kodekernel.site.entity.ProductCategory.builder().name("AI Services").build());
            productCategoryRepository.save(com.kodekernel.site.entity.ProductCategory.builder().name("Full-Stack").build());
            System.out.println("Product categories seeded.");
        }

        // Seed Default Product
        if (productRepository.count() == 0) {
            System.out.println("Seeding default store product...");
            com.kodekernel.site.entity.ProductCategory saas = productCategoryRepository.findByName("SaaS").orElse(null);
            if (saas != null) {
                com.kodekernel.site.entity.Product p = new com.kodekernel.site.entity.Product();
                p.setTitle("Modern SaaS Boilerplate");
                p.setPrice("499");
                p.setCategory(saas);
                p.setDescription("A production-ready React + Spring Boot foundation.");
                p.setLongDescription("Master architectural patterns with our elite SaaS starter kit.");
                p.setTags(List.of("React", "Spring Boot", "PostgreSQL"));
                p.setFeatures(List.of("Auth System", "Payment Ready", "Clean Arch"));
                p.setButtonText("Buy Source Code");
                p.setButtonLink("/contact");
                productRepository.save(p);
                System.out.println("Default store product seeded.");
            }
        }

        // TEMP: Promote all users to ADMIN for development/testing
        try {
            // Assign ADMIN role to all users in user_roles table
            jdbcTemplate.execute("INSERT INTO user_roles (user_id, role) SELECT id, 'ADMIN' FROM users WHERE id NOT IN (SELECT user_id FROM user_roles WHERE role = 'ADMIN')");

             // TEMP: Set first 4 users to be on team
            jdbcTemplate.execute("UPDATE users SET show_on_team = true, display_role = 'Team Member' WHERE id IN (SELECT id FROM users LIMIT 4)");
            
            System.out.println("Dev: All users promoted to ADMIN. Top 4 set to Team.");
        } catch (Exception e) {
            System.out.println("Error promoting users: " + e.getMessage());
        }

        System.out.println("Database Initializer completed.");
    }
}
