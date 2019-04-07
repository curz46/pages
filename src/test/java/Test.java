import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.container.ImmutableRatioContainer;
import me.dylancurzon.pages.element.container.ImmutableStackingContainer;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.event.TickEvent;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Vector2i;

public class Test {

    public static void main(String[] args) {
        Page page = new PageTemplate.Builder()
            .setFixedSize(Vector2i.of(256, 192))
            .doOnClick(e -> System.out.println("Page - Click!"))
            .add(p1 -> new ImmutableStackingContainer.Builder()
                .setTag("First container")
                .fillParentContainer()
                .doOnClick(e -> System.out.println("StackingContainer - Click!"))
                .add(p2 -> new ImmutableRatioContainer.Builder()
                    .setTag("Second container")
                    .setFixedSize(p2.getFixedSize().orElse(Vector2i.of(0, 0)))
                    .fillParentContainer()
                    .add(new ImmutableStackingContainer.Builder()
                        .doOnClick(e -> System.out.println("Smaller container - Click!"))
                        .fillAllocatedSize()
                        .build(), 1)
                    .add(new ImmutableStackingContainer.Builder()
                        .doOnClick(e -> System.out.println("Bigger container - Click!"))
                        .fillAllocatedSize()
                        .build(), 3)
                    .doOnCreate(element -> {
                        element.subscribe(MouseClickEvent.class, event -> {
                            System.out.println(element.getChildRatioMap());
                        });
                    })
                    .build())
                .build())
            .build()
            .create();
        page.click(Vector2i.of(0, 0), MouseButton.LEFT_MOUSE_BUTTON);
    }

}
