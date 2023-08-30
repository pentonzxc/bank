package clevertec.transaction.check;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.Function;

import clevertec.Account;
import clevertec.Bank;

public class TransactionPrinterFactory {
    private TransactionPrinterFactory() {
    }

    /**
     * Basic string view realization.
     * <p>
     * Represent check as block text.
     * 
     * @return TransactionPrinter<String>
     */
    static public TransactionPrinter<String> stringPrinter() {
        return new TransactionPrinter<String>() {
            final int width = 70;
            final int offset = 2;

            final String HeaderWord = "Банковский чек";
            final String CheckIdLabel = "Чек";
            final String TransactionTypeLabel = "Тип транзакции";
            final String OriginBankLabel = "Банк отправителя";
            final String TargetBankLabel = "Банк получателя";
            final String OriginAccountNumberLabel = "Счёт отправителя";
            final String TargetAccountNumberLabel = "Счёт получателя";
            final String TransferAmountLabel = "Сумма";

            @Override
            public String view(TransactionCheck check) {
                StringBuilder result = new StringBuilder();

                result = generateRowLine(result);
                newLineInPlace(result);

                result = generateHeaderRow(result, HeaderWord);
                newLineInPlace(result);

                result = generateRow(result, CheckIdLabel, check.getId(), id -> id.toString());
                newLineInPlace(result);

                String mutDateLabel = check.getCreatedAt().toLocalDate().toString();
                Function<LocalDateTime, LocalTime> f = LocalDateTime::toLocalTime;
                Function<Object, String> g = Object::toString;
                Function<String, String> z = time -> time.split("\\.")[0];
                result = generateRow(result, mutDateLabel, check.getCreatedAt(), f.andThen(g).andThen(z));
                newLineInPlace(result);

                result = generateRow(result, TransactionTypeLabel, check.getDescription(),
                        ActionDescription::getDescription);
                newLineInPlace(result);

                result = generateRow(result, OriginBankLabel, check.getOrigin().getBank().getName(),
                        Function.identity());
                newLineInPlace(result);

                result = generateRow(result, TargetBankLabel, check.getTarget().getBank().getName(),
                        Function.identity());

                newLineInPlace(result);

                result = generateRow(result, OriginAccountNumberLabel, check.getOrigin().getAccountNumber(),
                        Function.identity());
                newLineInPlace(result);

                result = generateRow(result, TargetAccountNumberLabel, check.getTarget().getAccountNumber(),
                        Function.identity());

                newLineInPlace(result);

                Function<Double, String> f1 = money -> String.format("%.2f", money).concat(" ").concat("BYN");
                result = generateRow(result, TransferAmountLabel, check.getTransferAmount(), f1);
                newLineInPlace(result);

                result = result.deleteCharAt(result.length() - 1);
                result = generateRowLine(result);

                return result.toString();
            }

            private void newLineInPlace(StringBuilder builder) {
                builder.append("\n").append("\n").append("|");
            }

            private void offsetInPlace(StringBuilder builder) {
                builder.append(" ").append(" ");
            }

            private void endInPlace(StringBuilder builder) {
                builder.append("|");
            }

            private StringBuilder generateRowLine(StringBuilder builder) {
                for (int i = 0; i < width + 3; ++i) {
                    builder.append("-");
                }
                return builder;
            }

            private <T> StringBuilder generateRow(
                    StringBuilder builder,
                    String label,
                    T value,
                    Function<T, String> transformer) {
                String transformed = transformer.apply(value);
                offsetInPlace(builder);
                builder.append(label);
                builder.append(":");

                int width = this.width - (offset * 2 + label.length() + transformed.length());
                for (int i = 0; i < width; ++i) {
                    builder.append(" ");
                }

                builder.append(transformed);
                offsetInPlace(builder);
                endInPlace(builder);
                return builder;
            }

            private StringBuilder generateHeaderRow(StringBuilder builder, String header) {
                int width = (this.width - 2) / 2 - header.length() / 2;

                for (int i = 0; i < width; ++i) {
                    builder.append(" ");
                }

                builder.append(header);
                for (int i = 0; i < (this.width + 1) - ((this.width - 2) / 2 + header.length() / 2); ++i) {
                    builder.append(" ");
                }

                endInPlace(builder);
                return builder;
            }

        };
    }
}
