package br.com.clinic.service.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.jboss.crypto.CryptoUtil;

import br.com.clinic.model.security.UserSystem;
import br.com.clinic.service.UserServiceRemote;

@Singleton
public class UserService implements UserServiceRemote {

	/** Logger. */
	private Logger log = Logger.getLogger(UserService.class.getCanonicalName());

	@PersistenceContext(unitName = "ClinicPU")
	private EntityManager em;

	@Override
	public void save(UserSystem entity) {
		if (entity.getId() == null) {
			if (entity.getPassword() == null) {
				entity.setPassword("123");// senha default para 1o cadastro
			}
			encript(entity);
			em.persist(entity);
		} else {
			em.merge(entity);
		}
	}

	private void encript(UserSystem entity) {
		String password = entity.getPassword();
		entity.setPassword(CryptoUtil.createPasswordHash("MD5", CryptoUtil.BASE64_ENCODING, null, null, password));
	}

	@Override
	public void delete(UserSystem entity) {
		em.remove(entity);
	}

	@Override
	public List<UserSystem> findAll() {
		CriteriaQuery<UserSystem> query = em.getCriteriaBuilder().createQuery(UserSystem.class);
		query.select(query.from(UserSystem.class));

		List<UserSystem> lista = em.createQuery(query).getResultList();

		return lista;
	}

	@Override
	public UserSystem findById(Long id) {
		return em.find(UserSystem.class, id);
	}

	@Override
	public UserSystem findByEmail(String email) {
		UserSystem user = null;
		user = em.createNamedQuery("UserSystem.findByEmail", UserSystem.class).setParameter("email", email)
				.getSingleResult();
		return user;
	}

	@Override
	public List<UserSystem> findAllByActive(boolean actived) {
		List<UserSystem> users = null;
		users = em.createNamedQuery("UserSystem.findAllByActived", UserSystem.class).setParameter("actived", actived)
				.getResultList();
		return users;
	}

	@Override
	public List<UserSystem> findByLikeName(String likeName) {
		List<UserSystem> users = null;
		users = em.createNamedQuery("UserSystem.findByLikeName", UserSystem.class).setParameter("likeName", likeName)
				.getResultList();
		return users;
	}

	@Override
	public void changePassword(UserSystem entity) {
		encript(entity);
	}

}
