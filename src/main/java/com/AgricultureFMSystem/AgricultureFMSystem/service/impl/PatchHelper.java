package com.AgricultureFMSystem.AgricultureFMSystem.service.impl;

import com.AgricultureFMSystem.AgricultureFMSystem.exception.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

/**
 * Shared validation and type-conversion helpers for partial PATCH updates.
 * Translates malformed or missing values into HTTP 400 {@link BadRequestException}.
 */
final class PatchHelper {

    private PatchHelper() {
    }

    static String requireNonBlankString(String field, Object value) {
        if (!(value instanceof String str) || str.isBlank()) {
            throw new BadRequestException("Field '" + field + "' must be a non-blank string.");
        }
        return str.trim();
    }

    static String optionalString(String field, Object value) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof String str)) {
            throw new BadRequestException("Field '" + field + "' must be a string.");
        }
        return str;
    }

    static Boolean requireBoolean(String field, Object value) {
        if (value instanceof Boolean b) {
            return b;
        }
        if (value instanceof String str) {
            if ("true".equalsIgnoreCase(str)) return Boolean.TRUE;
            if ("false".equalsIgnoreCase(str)) return Boolean.FALSE;
        }
        throw new BadRequestException("Field '" + field + "' must be a boolean (true or false).");
    }

    static Double requireDouble(String field, Object value, boolean positive) {
        if (value == null) {
            throw new BadRequestException("Field '" + field + "' cannot be null.");
        }
        double parsed;
        try {
            parsed = (value instanceof Number n) ? n.doubleValue() : Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Field '" + field + "' must be a valid number.");
        }
        if (Double.isNaN(parsed) || Double.isInfinite(parsed) || (positive && parsed <= 0)) {
            throw new BadRequestException("Field '" + field + "' must be greater than 0.");
        }
        return parsed;
    }

    static Integer requireInteger(String field, Object value, boolean positive) {
        if (value == null) {
            throw new BadRequestException("Field '" + field + "' cannot be null.");
        }
        int parsed;
        try {
            if (value instanceof Integer i) {
                parsed = i;
            } else if (value instanceof Number n && n.doubleValue() == Math.floor(n.doubleValue())) {
                parsed = n.intValue();
            } else {
                parsed = Integer.parseInt(value.toString().trim());
            }
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Field '" + field + "' must be a valid integer.");
        }
        if (positive && parsed <= 0) {
            throw new BadRequestException("Field '" + field + "' must be greater than 0.");
        }
        return parsed;
    }

    static Long requireLong(String field, Object value) {
        if (value == null) {
            throw new BadRequestException("Field '" + field + "' cannot be null.");
        }
        try {
            if (value instanceof Long l) {
                return l;
            }
            if (value instanceof Number n && n.doubleValue() == Math.floor(n.doubleValue())) {
                return n.longValue();
            }
            return Long.parseLong(value.toString().trim());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Field '" + field + "' must be a valid integer ID.");
        }
    }

    static BigDecimal requireBigDecimal(String field, Object value, boolean positive) {
        if (value == null) {
            throw new BadRequestException("Field '" + field + "' cannot be null.");
        }
        BigDecimal parsed;
        try {
            parsed = new BigDecimal(value.toString().trim());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Field '" + field + "' must be a valid decimal number.");
        }
        if (positive && parsed.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Field '" + field + "' must be greater than 0.");
        }
        return parsed;
    }

    static LocalDate requireLocalDate(String field, Object value) {
        if (value == null) {
            throw new BadRequestException("Field '" + field + "' cannot be null.");
        }
        return parseDate(field, value);
    }

    static LocalDate optionalLocalDate(String field, Object value) {
        return value == null ? null : parseDate(field, value);
    }

    private static LocalDate parseDate(String field, Object value) {
        if (!(value instanceof String str)) {
            throw new BadRequestException("Field '" + field + "' must be an ISO date string (YYYY-MM-DD).");
        }
        try {
            return LocalDate.parse(str.trim());
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Field '" + field + "' has invalid date format (expected YYYY-MM-DD).");
        }
    }

    static <E extends Enum<E>> E requireEnum(String field, Object value, Class<E> enumType) {
        if (value == null) {
            throw new BadRequestException("Field '" + field + "' cannot be null.");
        }
        String raw = value.toString().trim();
        for (E constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(raw)) {
                return constant;
            }
        }
        throw new BadRequestException("Invalid value '" + raw + "' for '" + field
                + "'. Allowed values: " + Arrays.toString(enumType.getEnumConstants()));
    }
}

