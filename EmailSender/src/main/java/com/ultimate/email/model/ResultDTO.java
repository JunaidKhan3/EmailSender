/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ultimate.email.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author JunaidKhan
 */
@Data
@AllArgsConstructor

public class ResultDTO {

    private Integer errNo;
    private String errMsg;

    public ResultDTO(String errMsg) {
        super();
        this.errMsg = errMsg;
    }
}
