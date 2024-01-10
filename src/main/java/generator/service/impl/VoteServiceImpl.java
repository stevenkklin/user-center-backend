package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Vote;
import generator.service.VoteService;
import generator.mapper.VoteMapper;
import org.springframework.stereotype.Service;

/**
* @author 73632
* @description 针对表【vote(用户)】的数据库操作Service实现
* @createDate 2024-01-10 00:32:04
*/
@Service
public class VoteServiceImpl extends ServiceImpl<VoteMapper, Vote>
    implements VoteService{

}




