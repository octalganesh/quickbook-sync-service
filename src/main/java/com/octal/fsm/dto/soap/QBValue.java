package com.octal.fsm.dto.soap;

import lombok.Data;

@Data
public class QBValue<T>  {
    private T value;
}
