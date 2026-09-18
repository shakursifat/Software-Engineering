import java.util.ArrayList;
import java.util.List;


// =================================
// Mediator
// =================================

interface ChatMediator {

    void sendMessage(String message, User sender);

    void addUser(User user);
}


// =================================
// Colleague
// =================================

abstract class User {

    protected ChatMediator mediator;
    protected String name;

    public User(
            ChatMediator mediator,
            String name) {

        this.mediator = mediator;
        this.name = name;
    }

    public abstract void send(String message);

    public abstract void receive(String message);
}


// =================================
// Concrete Colleague
// =================================

class ChatUser extends User {

    public ChatUser(
            ChatMediator mediator,
            String name) {

        super(mediator, name);
    }

    @Override
    public void send(String message) {

        System.out.println(
                name + " sends: " + message
        );

        mediator.sendMessage(
                message,
                this
        );
    }

    @Override
    public void receive(String message) {

        System.out.println(
                name + " receives: " + message
        );
    }
}


// =================================
// Concrete Mediator
// =================================

class ChatRoom implements ChatMediator {

    private List<User> users =
            new ArrayList<>();

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void sendMessage(
            String message,
            User sender) {

        for (User user : users) {

            if (user != sender) {

                user.receive(
                        sender.name
                        + ": "
                        + message
                );
            }
        }
    }
}


// =================================
// Client
// =================================

public class Chat{

    public static void main(String[] args) {

        ChatMediator chatRoom =
                new ChatRoom();


        User alice =
                new ChatUser(
                        chatRoom,
                        "Alice"
                );

        User bob =
                new ChatUser(
                        chatRoom,
                        "Bob"
                );

        User charlie =
                new ChatUser(
                        chatRoom,
                        "Charlie"
                );


        chatRoom.addUser(alice);
        chatRoom.addUser(bob);
        chatRoom.addUser(charlie);


        alice.send("Hello everyone!");

        System.out.println();

        bob.send("Hi Alice!");
    }
}
