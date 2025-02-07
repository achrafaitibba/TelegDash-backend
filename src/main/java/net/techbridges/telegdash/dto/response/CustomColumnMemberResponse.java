package net.techbridges.telegdash.dto.response;

import java.util.List;

public record CustomColumnMemberResponse(
        MemberResponse memberDetails,
        List<ValueResponse> customColumnValue
) {
}
