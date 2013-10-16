package be.noselus.repository;

import be.noselus.db.MyBatisUtils;
import be.noselus.model.Assembly;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

public class AssemblyRegistryMyBatis implements AssemblyRegistry {

    @Override
    public Assembly findId(final int id) {
        SqlSessionFactory sqlSessionFactory = MyBatisUtils.getSqlSessionFactory();
        final SqlSession sqlSession = sqlSessionFactory.openSession();
        final AssemblyRegistry mapper = sqlSession.getMapper(AssemblyRegistry.class);
        try {
            return mapper.findId(id);
        } finally {
            sqlSession.close();
        }
    }
}
