package com.hys.exam.springbatch_app_10.app.base.initData;

import com.hys.exam.springbatch_app_10.app.cart.entity.CartItem;
import com.hys.exam.springbatch_app_10.app.cart.service.CartService;
import com.hys.exam.springbatch_app_10.app.member.service.MemberService;
import com.hys.exam.springbatch_app_10.app.member.entity.Member;
import com.hys.exam.springbatch_app_10.app.order.entity.Order;
import com.hys.exam.springbatch_app_10.app.order.service.OrderService;
import com.hys.exam.springbatch_app_10.app.product.entity.Product;
import com.hys.exam.springbatch_app_10.app.product.service.ProductService;
import com.hys.exam.springbatch_app_10.app.song.entity.Song;
import com.hys.exam.springbatch_app_10.app.song.service.SongService;

import java.util.List;

public interface InitDataBefore {
    default void before(
            MemberService memberService,
            SongService songService,
            ProductService productService,
            CartService cartService,
            OrderService orderService) {
        class Helper {
            public Order order(Member member, List<Product> products) {
                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    cartService.addItem(member, product);
                }
                return orderService.createFromCart(member);
            }
        }
        Helper helper = new Helper();
        Member member1 = memberService.join("user1", "1234", "user1@test.com");
        Member member2 = memberService.join("user2", "1234", "user2@test.com");

        Song song1 = songService.create(member1, "노래 1", "내용 1");
        Song song2 = songService.create(member1, "노래 2", "내용 2");
        Song song3 = songService.create(member2, "노래 3", "내용 3");
        Song song4 = songService.create(member2, "노래 4", "내용 4");
        Song song5 = songService.create(member1, "노래 5", "내용 5");
        Song song6 = songService.create(member1, "노래 6", "내용 6");
        Song song7 = songService.create(member2, "노래 7", "내용 7");
        Song song8 = songService.create(member2, "노래 8", "내용 8");

        Product product1 = productService.create(song1, "상품 1", 1_900);
        Product product2 = productService.create(song3, "상품 2", 2_900);
        Product product3 = productService.create(song5, "상품 3", 3_900);
        Product product4 = productService.create(song7, "상품 4", 4_900);
        Product product5 = productService.create(song8, "상품 5", 5_900);

//        productService.modify(product1, "상품 1-1", 3_300);

        CartItem cartItem1 = cartService.addItem(member1, product1);
        CartItem cartItem2 = cartService.addItem(member1, product2);
        CartItem cartItem3 = cartService.addItem(member2, product3);
        CartItem cartItem4 = cartService.addItem(member2, product4);

        memberService.addCash(member1, 10_000, "충전__무통장입금");
        memberService.addCash(member1, 20_000, "충전__무통장입금");
        memberService.addCash(member1, -5_000, "출금__일반");
        memberService.addCash(member1, 1_000_000, "충전__무통장입금");
        memberService.addCash(member2, 2_000_000, "충전__무통장입금");

    }
}