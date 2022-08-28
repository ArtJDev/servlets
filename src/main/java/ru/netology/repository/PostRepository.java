package ru.netology.repository;

import ru.netology.model.Post;
import ru.netology.exception.NotFoundException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository {
    private final AtomicLong newId = new AtomicLong();                 //хранит id по порядку
    private final Set<Long> usedId = new CopyOnWriteArraySet<>();      //хранит список использованных id
    private final Set<Long> freeId = new CopyOnWriteArraySet<>();      //хранит список свободных id
    private final List<Post> posts = new CopyOnWriteArrayList<>();

    public List<Post> all() {
        return posts;
    }

    public Optional<Post> getById(long id) {
        for (Post post : posts) {
            if (post.getId() == id) {
                return Optional.of(post);
            }
        }
        return Optional.empty();
    }

    public Post save(Post post) {
        if (newId.get() == 0) {         //сразу присваиваем newId = 1
            newId.set(1);
        }

        if (posts.size() == 0) {            //если список постов пустой, то
            if (post.getId() == 0) {            //если пришел пост с id = 0
                post.setId(newId.get());            //присваиваем посту id = 1
                posts.add(post);                    //и добавляем его в лист
                usedId.add(newId.get());            //добавляем newId в список использованных id
                newId.incrementAndGet();            //увеличиваем newId на 1
            } else {                            //иначе, если id поста != 0, то
                posts.add(post);                    //просто добавляем пост с этим id в список постов
                usedId.add(post.getId());           //добавляем newId в список использованных id
            }
        } else {                            //иначе если список постов не пустой, то
            if (post.getId() == 0) {            //если пришел пост с id = 0, то
                if (!freeId.isEmpty()) {            //смотрим есть ли свободные номера для id, если да, то
                    post.setId(freeId.stream().min(Comparator.naturalOrder()).get());   //присваиваем посту минимальный id из списка свободных
                    freeId.remove(post.getId());        //удаляем номер id из списка свободных
                    posts.add(post);                    //добавляем пост в список постов
                    usedId.add(post.getId());           //добавляем номер id в список использованных
                } else {                            //иначе, если свободных номеров для id нет, то
                    while (usedId.contains(newId.get())) {  //в цикле смотрим, содержится ли следующий по порядку id в списке использованных
                        newId.incrementAndGet();            //каждый раз увеличивая его на 1
                    }                                       //как только находится следующий не использованный по порядку номер id, выходим из цикла
                    post.setId(newId.get());            //присваиваем посту новый newId
                    posts.add(post);                    //добавляем пост в список постов
                    usedId.add(newId.get());            //добавляем номер нового id в список использованных
                }
            } else {                            //иначе, если пришел пост с id != 0, то
                if (usedId.contains(post.getId())) {  //смотрим, есть ли уже такой id в списке использованных, если да, то
                    for (Post post1 : posts) {           //ищем пост с таким же id
                        if (post1.getId() == post.getId() && !post1.getContent().equals(post.getContent())) { //когда найдется пост с таким же id, сравниваем контент, если контент разный, то
                            posts.remove(post1);         //удаляем старый пост из списка
                            posts.add(post);             //добавляем новый пост в список
                        }                                //иначе ничего не делаем
                    }
                } else {                              //иначе, если поста с таки id не существует, то
                    posts.add(post);                     //добавляем пост в список
                    usedId.add(post.getId());            //добавляем номер id поста в список использованных
                }
            }
        }
        return post;
    }

    public void removeById(long id) {
        if (usedId.contains(id)) {          //если id содержится в списке использованных id, то
            for (Post post1 : posts) {          //ищем пост с таким id в списке постов
                if (post1.getId() == id) {          //если найден, то
                    posts.remove(post1);                //удаляем пост из списка
                    usedId.remove(id);                  //удаляем id из списка использованных
                    freeId.add(id);                     //добавляем id в список свободных
                }
            }
        } else {                            //иначе если поста с таким id нет в списке использованных
            throw new NotFoundException();      //выбрасываем ошибку в контроллер
        }
        if (posts.isEmpty()) {              //если удалили все посты из списка
            newId.set(1);                       //выставляем нумерацию на 1
            freeId.clear();                     //очищаем список свободных id
        }
    }
}