package com.tencent.supersonic.headless.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tencent.supersonic.auth.api.authentication.pojo.User;
import com.tencent.supersonic.common.pojo.enums.Text2SQLType;
import com.tencent.supersonic.common.util.ContextUtils;
import com.tencent.supersonic.headless.api.pojo.SchemaMapInfo;
import com.tencent.supersonic.headless.api.pojo.SemanticSchema;
import com.tencent.supersonic.headless.api.pojo.enums.MapModeEnum;
import com.tencent.supersonic.headless.api.pojo.request.QueryFilters;
import com.tencent.supersonic.headless.core.chat.query.SemanticQuery;
import com.tencent.supersonic.headless.core.config.OptimizationConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryContext {

    private String queryText;
    private Integer chatId;
    private Set<Long> dataSetIds;
    private Map<Long, List<Long>> modelIdToDataSetIds;
    private User user;
    private boolean saveAnswer;
    private Text2SQLType text2SQLType = Text2SQLType.RULE_AND_LLM;
    private QueryFilters queryFilters;
    private List<SemanticQuery> candidateQueries = new ArrayList<>();
    private SchemaMapInfo mapInfo = new SchemaMapInfo();
    private MapModeEnum mapModeEnum = MapModeEnum.STRICT;
    @JsonIgnore
    private SemanticSchema semanticSchema;

    public List<SemanticQuery> getCandidateQueries() {
        OptimizationConfig optimizationConfig = ContextUtils.getBean(OptimizationConfig.class);
        Integer parseShowCount = optimizationConfig.getParseShowCount();
        candidateQueries = candidateQueries.stream()
                .sorted(Comparator.comparing(semanticQuery -> semanticQuery.getParseInfo().getScore(),
                        Comparator.reverseOrder()))
                .limit(parseShowCount)
                .collect(Collectors.toList());
        return candidateQueries;
    }
}
