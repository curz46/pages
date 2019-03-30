import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.container.ImmutableContainer;
import me.dylancurzon.pages.element.container.LayoutImmutableContainer;
import me.dylancurzon.pages.util.Vector2i;

public class Test {

    public static void main(String[] args) {
        Page page = PageTemplate.builder()
            .setSize(Vector2i.of(256, 192))
            .add(p1 -> ImmutableContainer.builder()
                .setSize(p1.getSize())
                .add(p2 -> LayoutImmutableContainer.builder()
                    .setSize(p2.getSize())
                    .add(1, ImmutableContainer.builder()
                        .setZ(20)
                        .doOnCreate(element -> {
                            System.out.println(element.getZ());
                        })
                        .build())
                    .build())
                .build())
            .build()
            .create();
    }

}
