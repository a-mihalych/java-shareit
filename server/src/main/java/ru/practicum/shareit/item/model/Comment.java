package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
@ToString
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Integer id;
    @Column(name = "comment_text")
    String text;
    @ManyToOne
    @JoinColumn(name = "item_id")
    Item item;
    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;
    LocalDateTime created;
}
