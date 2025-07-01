package gui;


public enum CostumeEvent {
    //Events
    SPRING_FESTIVALS("Spring Festivals (Hanami, Easter)"),
    GRADUATION_CEREMONY("Graduation / Entrance Ceremony"),
    SUMMER_FESTIVALS("Summer Festivals / Fireworks"),
    HALLOWEEN("Halloween"),
    CHRISTMAS("Christmas"),
    YEAR_END_PARTIES("Year-End / New Year Parties"),
    NEW_YEARS_CELEBRATION("New Year's Celebration"),
    WEDDING_GUEST("Wedding (Guest)"),
    WEDDING_RECEPTION("Wedding (Reception / After-party)"),
    COMING_OF_AGE_CEREMONY("Coming-of-Age Ceremony"),
    SHICHI_GO_SAN("Shichi-Go-San Festival"),
    BIRTHDAYS_ANNIVERSARIES("Birthdays / Anniversaries"),
    STAGE_PERFORMANCES("Stage Performances / Theatre"),
    COSPLAY_EVENTS("Cosplay / Photo Shoots");

    private final String displayName;  //for display in the UI


    CostumeEvent(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }


    @Override
    public String toString() {
        return this.displayName;
    }
}