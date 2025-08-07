package org.libre.ai.modules.rag.mapper;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.libre.ai.modules.rag.entity.AigcMessage;

import java.util.List;

@Mapper
public interface AigcMessageMapper extends BaseMapper<AigcMessage> {

	// @Select("""
	// WITH RECURSIVE DateRange AS (
	// SELECT CURDATE() AS date
	// UNION ALL
	// SELECT date - INTERVAL 1 DAY
	// FROM DateRange
	// WHERE date > DATE_SUB(CURDATE(), INTERVAL 31 DAY)
	// )
	// SELECT
	// d.date,
	// COALESCE(COUNT(*), 0) AS tokens
	// FROM
	// DateRange d
	// LEFT JOIN
	// aigc_message m
	// ON
	// DATE(m.create_time) = d.date
	// AND m.role = 'assistant'
	// GROUP BY
	// d.date
	// ORDER BY
	// d.date DESC;
	// """)
	// List<Dict> getReqChartBy30();

	@Select("""
			    SELECT
			        TO_CHAR(create_time, 'YYYY-MM') as month,
			        COALESCE(COUNT(*), 0) as count
			    FROM
			        aigc_message
			    WHERE
			        create_time >= CURRENT_DATE - INTERVAL '1 year'
			        AND role = 'assistant'
			    GROUP BY
			        month
			    ORDER BY
			        month ASC;
			""")
	List<Dict> getReqChart();

	@Select("""
			    WITH RECURSIVE DateRange AS (
			        SELECT CURRENT_DATE::date AS date
			        UNION ALL
			        SELECT (date - INTERVAL '1 day')::date
			        FROM DateRange
			        WHERE date > CURRENT_DATE - INTERVAL '31 days'
			    )
			    SELECT
			        d.date,
			        COALESCE(SUM(m.tokens), 0) AS tokens
			    FROM
			        DateRange d
			    LEFT JOIN
			        aigc_message m
			    ON
			        DATE(m.create_time) = d.date
			        AND m.role = 'assistant'
			    GROUP BY
			        d.date
			    ORDER BY
			        d.date DESC;
			""")
	List<Dict> getTokenChartBy30();

	@Select("""
			    SELECT
			        TO_CHAR(create_time, 'YYYY-MM') as month,
			        COALESCE(SUM(tokens), 0) as count
			    FROM
			        aigc_message
			    WHERE
			        create_time >= CURRENT_DATE - INTERVAL '1 year'
			        AND role = 'assistant'
			    GROUP BY
			        month
			    ORDER BY
			        month ASC;
			""")
	List<Dict> getTokenChart();

	@Select("""
			    SELECT
			        COALESCE(COUNT(*), 0) AS totalReq,
			        COALESCE(SUM( CASE WHEN DATE ( create_time ) = CURRENT_DATE THEN 1 ELSE 0 END ), 0) AS curReq
			    FROM
			        aigc_message
			    WHERE
			        role = 'assistant'
			""")
	Dict getCount();

	@Select("""
			    SELECT
			        COALESCE(SUM(tokens), 0) AS totalToken,
			        COALESCE(SUM(CASE WHEN DATE(create_time) = CURRENT_DATE THEN tokens ELSE 0 END), 0) AS curToken
			    FROM
			        aigc_message;
			""")
	Dict getTotalSum();

}
