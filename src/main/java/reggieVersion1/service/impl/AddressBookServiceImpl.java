package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import reggieVersion1.domain.AddressBook;
import reggieVersion1.service.AddressBookService;
import reggieVersion1.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author White
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2023-01-29 13:19:28
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




