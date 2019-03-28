import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.container.ImmutableContainer;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.util.Vector2i;

public class Test {

    public static void main(String[] args) {
        Page page = PageTemplate.builder()
            .setSize(Vector2i.of(256, 192))
            .add(ImmutableContainer.builder()
                .setSize(Vector2i.of(50, 50))
                .doOnCreate(container -> {
                    container.subscribe(MouseClickEvent.class, event -> {
                        System.out.println("Container - Clicked: " + event);
                    });
                })
                .build())
            .build()
            .asMutable();

        page.post(new MouseClickEvent(Vector2i.of(30, 30), MouseClickEvent.Button.LEFT_MOUSE_BUTTON));
    }

}
