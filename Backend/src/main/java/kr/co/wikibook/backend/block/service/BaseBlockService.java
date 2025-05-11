package kr.co.wikibook.backend.block.service;

import kr.co.wikibook.backend.block.mapper.BlockMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseBlockService implements BlockService {

    @Autowired
    private BlockMapper blockMapper;

    @Override
    public Integer addBlock(String token) {
        return blockMapper.addBlock(token);
    }

    @Override
    public boolean hasBlock(String token) {
        return blockMapper.hasBlock(token);
    }


}
