package clevertec.transaction.check;

public enum ActionDescription {
    ACCOUNT_TRANSFER_ADD("Пополнение"),
    ACCOUNT_TRANSFER_SUB("Вывод"),
    ACCOUNT_ACCOUNT_TRANSFER("Перевод на счёт");

    private final String description;

    private ActionDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
