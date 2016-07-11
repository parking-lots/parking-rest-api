package parking.utils;

public enum EmailDomain {
    SWEDBANK_LT("swedbank.lt");

    private String emailDomain;

    EmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    public String getDomain() {
        return emailDomain;
    }
}
