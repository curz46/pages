import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.container.ImmutableContainer;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Vector2i;

public class Test {

    public static void main(String[] args) {
        Page page = PageTemplate.builder()
            .setSize(Vector2i.of(256, 192))
            .add(ImmutableContainer.builder()
                .setSize(Vector2i.of(50, 50))
                .doOnClick(event -> System.out.println("Container - Clicked (1): " + event))
                .doOnCreate(element -> {
                    element.doOnClick(event -> System.out.println("Container - Clicked (2): " + event));
                })
                .build())
            .build()
            .asMutable();

        page.click(Vector2i.of(30, 30), MouseButton.LEFT_MOUSE_BUTTON);
    }

}
