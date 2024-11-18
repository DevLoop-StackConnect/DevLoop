package com.devloop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.devloop.party.repository.jpa",
                "com.devloop.lecture.repository.jpa",
                "com.devloop.community.repository.jpa",
                "com.devloop.pwt.repository.jpa",
                "com.devloop.attachment.repository",
                "com.devloop.user.repository",
                "com.devloop.cart.repository",
                "com.devloop.tutor.repository",
                "com.devloop.product.repository",
                "com.devloop.communitycomment.repository",
                "com.devloop.partycomment.repository",
                "com.devloop.purchase.repository",
                "com.devloop.order.repository",
                "com.devloop.stock.repository",
                "com.devloop.scheduleboard.repository",
                "com.devloop.lecturereview.repository",
                "com.devloop.notification.repository",
                "com.devloop.payment.repository",
                "com.devloop.scheduletodo.repository"
        }
)
public class JpaConfig {
}
