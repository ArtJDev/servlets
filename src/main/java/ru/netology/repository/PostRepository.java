package ru.netology.repository;

import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.stream.Stream;

// Stub
public class PostRepository {
    private final AtomicLong newId = new AtomicLong();
    private final Set<Long> idList = new CopyOnWriteArraySet<>();
    private final List<Post> posts = new CopyOnWriteArrayList<>();

    public List<Post> all() {
        return posts;
    }

    public Optional<Post> getById(long id) {
        //добавить if на проверку номера id
        for (Post post : posts) {
            if (post.getId() == id) {
                return Optional.of(post);
            }
        }
        return Optional.empty();
    }

    public Post save(Post post) {
        if (posts.size() == 0 && post.getId() == 0) {
            post.setId(1);
            posts.add(post);
            newId.set(post.getId());
            idList.add(newId.get());
        }
        if (posts.size() == 0 && post.getId() != 0) {
            posts.add(post);
            newId.set(post.getId());
            idList.add(newId.get());
        }
        if (posts.size() != 0 && post.getId() == 0) {
//            long nextId = idList.stream().min(Comparator.naturalOrder()).get();
//            while (!idList.contains(nextId)) {
//                post.setId(nextId);
//                posts.add(post);
//                nextId++;
//            }
        }
        return post;
    }

    public void removeById(long id) {

    }
}
