package si.fri.rso.uniborrow.loans.services.items;

import lombok.Data;

@Data
public class Item {
    private int imageId;
    private String category;
    private String description;
    private int score;
    private String status;
    private String title;
    private String uri;
    private String userId;
}
