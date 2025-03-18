package com.springboot.gotgam.dto.report;

import com.springboot.gotgam.entity.mysql.Member;
import com.springboot.gotgam.entity.mysql.Report;
import com.springboot.gotgam.constant.Type;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportReqDto {
    private String reason;
    private String reporter;
    private String reported;
    private String reportEntity;
    private Type reportType;

    public Report toEntity(String reason, Member reporter, Member reported, String reportEntity, Type reportType) {
        return Report.builder()
                .reason(reason)
                .reporter(reporter)
                .reported(reported)
                .reportEntity(reportEntity)
                .reportType(reportType)
                .build();
    }
}
